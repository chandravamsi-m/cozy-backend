package com.cozycreations.backend.controllers;

import com.cozycreations.backend.services.CatalogueService;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/catalogue")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class CatalogueController {

    @Autowired
    private CatalogueService catalogueService;

    @GetMapping("/generate")
    public ResponseEntity<?> generateCatalogue() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<Map<String, Object>> products = new ArrayList<>();
            
            var future = db.collection("products").get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            
            for (QueryDocumentSnapshot document : documents) {
                Map<String, Object> data = document.getData();
                
                // Helper logic: optimize cloudinary URL for PDF rendering
                String imageUrl = (String) data.get("imageUrl");
                if (imageUrl != null && imageUrl.contains("cloudinary.com")) {
                    String optimizedUrl = imageUrl.replace("/upload/", "/upload/w_300,h_300,c_fill,f_auto,q_auto:good/");
                    data.put("optimizedImageUrl", optimizedUrl);
                } else {
                    data.put("optimizedImageUrl", imageUrl);
                }
                
                // Add formatting parameters expected by Thymeleaf
                data.put("fontSize", 24);
                data.put("badgeText", "Pack of 1 - ₹" + data.get("price"));
                data.put("descriptionHtml", "Handpoured Soy Candle");
                
                products.add(data);
            }
            
            byte[] pdfBytes = catalogueService.generatePdfCatalogue(products, "All Products");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=catalogue.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
