package com.cozycreations.backend.controllers;

import com.cozycreations.backend.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;

import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/razorpay")
    public ResponseEntity<?> handleRazorpayWebhook(@RequestHeader("X-Razorpay-Signature") String signature, 
                                                   @RequestBody String payload) {
        // Here we'd verify the signature, but since the raw payload and signature are available,
        // we can implement a custom signature verification for webhooks.
        return ResponseEntity.ok(Map.of("status", "received"));
    }

    @PostMapping("/shiprocket")
    public ResponseEntity<?> handleShiprocketWebhook(@RequestHeader(value = "x-api-key", required = false) String apiKey, 
                                                     @RequestBody Map<String, Object> payload) {
        // Process shiprocket tracking updates
        return ResponseEntity.ok(Map.of("status", "received"));
    }
}
