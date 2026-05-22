package com.cozycreations.backend.controllers;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class SettingsController {

    @Value("${cozy.enable-shipping-fee:true}")
    private boolean isShippingFeeEnabled;

    @GetMapping("/public")
    public ResponseEntity<?> getPublicSettings() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var deliveryDoc = db.collection("settings").document("delivery").get().get();
            var paymentDoc = db.collection("settings").document("payment").get().get();
            var offerDoc = db.collection("settings").document("offerBanner").get().get();

            Map<String, Object> delivery = deliveryDoc.exists() ? new HashMap<>(deliveryDoc.getData()) : new HashMap<>();
            delivery.put("isShippingFeeEnabled", isShippingFeeEnabled);

            Map<String, Object> payment = paymentDoc.exists() ? paymentDoc.getData() : Map.of("isCodEnabled", true, "isPlatformFeeEnabled", false, "platformFee", 0);
            Map<String, Object> offer = offerDoc.exists() ? offerDoc.getData() : Map.of("isActive", false);

            return ResponseEntity.ok(Map.of(
                "delivery", delivery,
                "payment", payment,
                "offer", offer
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> getSettings(@PathVariable String type) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            // Map 'public' to 'global' if necessary, else just use the type.
            String docId = "public".equals(type) ? "global" : type;
            var docRef = db.collection("settings").document(docId).get().get();
            
            if (docRef.exists()) {
                if ("payment".equals(type) || "delivery".equals(type)) {
                    return ResponseEntity.ok(Map.of(type, docRef.getData()));
                }
                return ResponseEntity.ok(docRef.getData());
            } else {
                return ResponseEntity.ok(Map.of());
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
