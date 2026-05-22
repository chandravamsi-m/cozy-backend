package com.cozycreations.backend.controllers;

import com.cozycreations.backend.services.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shipping")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @GetMapping("/check-serviceability")
    public ResponseEntity<?> checkServiceability(
            @RequestParam String pincode,
            @RequestParam(required = false, defaultValue = "1.0") double weight,
            @RequestParam(required = false, defaultValue = "0") int cod,
            @RequestParam(required = false, defaultValue = "0.0") double amount,
            @RequestParam(required = false, defaultValue = "10") int l,
            @RequestParam(required = false, defaultValue = "10") int w,
            @RequestParam(required = false, defaultValue = "10") int h) {
        try {
            boolean isCod = cod == 1;
            Map<String, Object> result = shippingService.checkServiceability(pincode, weight, isCod, amount, l, w, h);
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
