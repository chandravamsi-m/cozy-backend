package com.cozycreations.backend.services;

import com.cozycreations.backend.models.Order;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired(required = false)
    private RazorpayClient razorpayClient;

    @Value("${cozy.razorpay.key-secret}")
    private String keySecret;

    public com.razorpay.Order createRazorpayOrder(Double amount) throws RazorpayException {
        if (razorpayClient == null) throw new IllegalStateException("Razorpay not configured");
        JSONObject options = new JSONObject();
        options.put("amount", Math.round(amount * 100));
        options.put("currency", "INR");
        options.put("receipt", "order_" + System.currentTimeMillis());
        return razorpayClient.orders.create(options);
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(keySecret.getBytes(), "HmacSHA256");
            hmac.init(secretKey);
            String payload = orderId + "|" + paymentId;
            byte[] hash = hmac.doFinal(payload.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    public String createOrderRecord(Map<String, Object> orderData, String paymentId, String paymentOrderId, String userId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String newOrderId = UUID.randomUUID().toString();
        
        Map<String, Object> data = new HashMap<>(orderData);
        data.put("id", newOrderId);
        if (userId != null) data.put("userId", userId);
        if (paymentId != null) data.put("paymentId", paymentId);
        if (paymentOrderId != null) data.put("paymentOrderId", paymentOrderId);
        if (!data.containsKey("status")) data.put("status", "new");
        if (!data.containsKey("createdAt")) data.put("createdAt", com.google.cloud.firestore.FieldValue.serverTimestamp());
        
        db.collection("orders").document(newOrderId).set(data).get(); // wait for it
        return newOrderId;
    }
}
