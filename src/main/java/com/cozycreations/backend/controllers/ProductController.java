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
@RequestMapping("/api/products")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class ProductController {

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<Map<String, Object>> products = new ArrayList<>();
            
            var future = db.collection("products").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId());
                products.add(data);
            }
            
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var docRef = db.collection("products").document(id).get().get();
            if (docRef.exists()) {
                Map<String, Object> data = docRef.getData();
                data.put("id", docRef.getId());
                return ResponseEntity.ok(data);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
