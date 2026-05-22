package com.cozycreations.backend.controllers;

import com.cozycreations.backend.models.Order;
import com.cozycreations.backend.services.PaymentService;
import com.cozycreations.backend.services.ShippingService;
import com.cozycreations.backend.services.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class OrderController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private WhatsappService whatsappService;

    @Value("${cozy.razorpay.key-id:rzp_test_Rt3GuAAKv0iIDK}")
    private String razorpayKeyId;

    private double calculateTotal(Map<String, Object> payload) throws Exception {
        com.google.cloud.firestore.Firestore db = com.google.firebase.cloud.FirestoreClient.getFirestore();
        java.util.List<Map<String, Object>> items = (java.util.List<Map<String, Object>>) payload.get("items");
        double total = 0;
        if (items != null) {
            for (Map<String, Object> item : items) {
                String productId = (String) item.get("productId");
                Object quantityObj = item.get("quantity");
                int quantity = 1;
                if (quantityObj != null) {
                    try { quantity = Integer.parseInt(quantityObj.toString()); } catch (Exception ignored) {}
                }
                var doc = db.collection("products").document(productId).get().get();
                if (doc.exists() && doc.getDouble("price") != null) {
                    total += (doc.getDouble("price") * quantity);
                }
            }
        }
        
        var paymentDoc = db.collection("settings").document("payment").get().get();
        if (paymentDoc.exists() && Boolean.TRUE.equals(paymentDoc.getBoolean("isPlatformFeeEnabled"))) {
            Double platformFee = paymentDoc.getDouble("platformFee");
            if (platformFee != null) {
                total += platformFee;
            }
        }
        
        var deliveryDoc = db.collection("settings").document("delivery").get().get();
        if (deliveryDoc.exists()) {
            Double amount = deliveryDoc.getDouble("amount");
            if (amount != null) {
                total += amount;
            }
        }
        
        return total;
    }

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> payload) {
        try {
            double total = calculateTotal(payload);
            com.razorpay.Order rzpOrder = paymentService.createRazorpayOrder(total);
            
            com.google.cloud.firestore.Firestore db = com.google.firebase.cloud.FirestoreClient.getFirestore();
            Map<String, Object> attempt = new java.util.HashMap<>();
            attempt.put("status", "pending");
            attempt.put("orderData", payload);
            attempt.put("amount", total);
            db.collection("paymentAttempts").document(rzpOrder.get("id")).set(attempt);

            Map<String, Object> responseMap = new java.util.HashMap<>();
            responseMap.put("orderId", rzpOrder.get("id"));
            responseMap.put("amount", rzpOrder.get("amount"));
            responseMap.put("currency", rzpOrder.get("currency"));
            responseMap.put("key", razorpayKeyId);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload, @RequestHeader(value="Authorization", required=false) String authHeader) {
        try {
            String razorpayOrderId = payload.get("razorpay_order_id");
            String razorpayPaymentId = payload.get("razorpay_payment_id");
            String razorpaySignature = payload.get("razorpay_signature");
            
            if (!paymentService.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid signature"));
            }
            
            com.google.cloud.firestore.Firestore db = com.google.firebase.cloud.FirestoreClient.getFirestore();
            var attemptDoc = db.collection("paymentAttempts").document(razorpayOrderId).get().get();
            if (!attemptDoc.exists()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Attempt not found"));
            }
            
            String userId = "guest";
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    com.google.firebase.auth.FirebaseToken token = com.google.firebase.auth.FirebaseAuth.getInstance().verifyIdToken(authHeader.substring(7));
                    userId = token.getUid();
                } catch (Exception ignored) {}
            }
            
            Map<String, Object> orderDataMap = (Map<String, Object>) attemptDoc.get("orderData");
            double total = attemptDoc.getDouble("amount");
            orderDataMap.put("total", total);
            orderDataMap.put("paymentMethod", "online");
            
            String orderId = paymentService.createOrderRecord(orderDataMap, razorpayPaymentId, razorpayOrderId, userId);
            
            Map<String, Object> update = new java.util.HashMap<>();
            update.put("status", "completed");
            update.put("orderId", orderId);
            db.collection("paymentAttempts").document(razorpayOrderId).update(update);
            
            return ResponseEntity.ok(Map.of("success", true, "orderId", orderId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }

    @PostMapping("/place-cod")
    public ResponseEntity<?> placeCod(@RequestBody Map<String, Object> payload, @RequestHeader(value="Authorization", required=false) String authHeader) {
        try {
            String userId = "guest";
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    com.google.firebase.auth.FirebaseToken token = com.google.firebase.auth.FirebaseAuth.getInstance().verifyIdToken(authHeader.substring(7));
                    userId = token.getUid();
                } catch (Exception ignored) {}
            }
            
            double total = calculateTotal(payload);
            payload.put("total", total);
            payload.put("paymentMethod", "cod");
            
            String orderId = paymentService.createOrderRecord(payload, null, null, userId);
            return ResponseEntity.ok(Map.of("success", true, "orderId", orderId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage() != null ? e.getMessage() : e.toString()));
        }
    }
}
