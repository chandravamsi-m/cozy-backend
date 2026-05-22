package com.cozycreations.backend.controllers;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class AdminController {

    @org.springframework.beans.factory.annotation.Autowired
    private com.cozycreations.backend.services.CatalogueService catalogueService;

    @GetMapping(value = "/generate-catalogue", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateCatalogue() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            java.util.List<Map<String, Object>> products = new java.util.ArrayList<>();
            var docs = db.collection("products").whereEqualTo("isActive", true).get().get().getDocuments();
            for (var doc : docs) {
                Map<String, Object> data = doc.getData();
                data.put("id", doc.getId());
                products.add(data);
            }
            byte[] pdf = catalogueService.generatePdfCatalogue(products, "Our Collection");
            return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"catalogue.pdf\"")
                .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body((e.getMessage() != null ? e.getMessage() : e.toString()).getBytes());
        }
    }

    @GetMapping(value = "/generate-bulk-catalogue", produces = org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateBulkCatalogue() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            java.util.List<Map<String, Object>> products = new java.util.ArrayList<>();
            var docs = db.collection("products").whereEqualTo("isActive", true).whereEqualTo("isBulk", true).get().get().getDocuments();
            for (var doc : docs) {
                Map<String, Object> data = doc.getData();
                data.put("id", doc.getId());
                products.add(data);
            }
            byte[] pdf = catalogueService.generatePdfCatalogue(products, "Corporate Gifting & Bulk Collection");
            return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bulk-catalogue.pdf\"")
                .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body((e.getMessage() != null ? e.getMessage() : e.toString()).getBytes());
        }
    }

    @GetMapping("/catalogue-status")
    public ResponseEntity<?> getCatalogueStatus() {
        return ResponseEntity.ok(Map.of("status", "ready"));
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            
            // Users
            int totalUsers = db.collection("users").get().get().size();
            
            // Products
            int totalProducts = db.collection("products").get().get().size();
            
            // Orders
            java.util.List<com.google.cloud.firestore.QueryDocumentSnapshot> orderDocs = 
                db.collection("orders").orderBy("createdAtIso", com.google.cloud.firestore.Query.Direction.DESCENDING).get().get().getDocuments();
            
            int totalOrders = orderDocs.size();
            int deliveredOrders = 0;
            double totalRevenue = 0.0;
            
            java.util.Map<String, Integer> statusCounts = new java.util.HashMap<>();
            java.util.List<Map<String, Object>> recentOrders = new java.util.ArrayList<>();
            
            for (int i = 0; i < orderDocs.size(); i++) {
                com.google.cloud.firestore.QueryDocumentSnapshot doc = orderDocs.get(i);
                Map<String, Object> data = doc.getData();
                data.put("id", doc.getId());
                
                String status = (String) data.getOrDefault("status", "pending");
                statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
                
                if ("delivered".equalsIgnoreCase(status)) {
                    deliveredOrders++;
                }
                
                if (!"cancelled".equalsIgnoreCase(status)) {
                    Object totalObj = data.get("total");
                    if (totalObj instanceof Number) {
                        totalRevenue += ((Number) totalObj).doubleValue();
                    }
                }
                
                if (i < 5) {
                    recentOrders.add(data);
                }
            }
            
            java.util.List<Map<String, Object>> ordersByStatus = new java.util.ArrayList<>();
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                if (!"delivered".equalsIgnoreCase(entry.getKey())) {
                    ordersByStatus.add(Map.of("name", entry.getKey(), "value", entry.getValue()));
                }
            }
            
            // Default structure expected by frontend
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalRevenue", totalRevenue);
            stats.put("totalOrders", totalOrders);
            stats.put("deliveredOrders", deliveredOrders);
            stats.put("totalUsers", totalUsers);
            stats.put("activeProducts", totalProducts);
            stats.put("salesTrend", Map.of("days", java.util.List.of(), "weeks", java.util.List.of(), "months", java.util.List.of()));
            stats.put("ordersByStatus", ordersByStatus);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", stats,
                    "recentOrders", recentOrders,
                    "adminName", "Admin"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/offers")
    public ResponseEntity<?> getAllOffers() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            java.util.List<Map<String, Object>> offers = new java.util.ArrayList<>();
            
            var future = db.collection("offers").get();
            java.util.List<com.google.cloud.firestore.QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (com.google.cloud.firestore.QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                data.put("id", document.getId());
                offers.add(data);
            }
            
            return ResponseEntity.ok(Map.of("offers", offers));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/offers")
    public ResponseEntity<?> createOffer(@RequestBody Map<String, Object> payload) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var docRef = db.collection("offers").document();
            payload.put("id", docRef.getId());
            docRef.set(payload);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/offers/{id}")
    public ResponseEntity<?> updateOffer(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection("offers").document(id).set(payload, com.google.cloud.firestore.SetOptions.merge());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/offers/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable String id) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection("offers").document(id).delete();
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/settings/{type}")
    public ResponseEntity<?> updateSettings(@PathVariable String type, @RequestBody Map<String, Object> payload) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            db.collection("settings").document(type).set(payload, com.google.cloud.firestore.SetOptions.merge());
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
