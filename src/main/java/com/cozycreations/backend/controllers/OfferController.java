package com.cozycreations.backend.controllers;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class OfferController {

    @GetMapping("/active")
    public ResponseEntity<?> getOffers() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<Map<String, Object>> offers = new ArrayList<>();
            
            var future = db.collection("offers").whereEqualTo("active", true).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId());
                offers.add(data);
            }
            
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/calculate-discount")
    public ResponseEntity<?> calculateDiscount(@RequestBody Map<String, Object> payload) {
        try {
            // Simplified logic: return 0 discount
            return ResponseEntity.ok(Map.of("discount", 0, "success", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
