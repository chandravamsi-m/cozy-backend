package com.cozycreations.backend.controllers;

import com.cozycreations.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/welcome-email")
    public ResponseEntity<?> sendWelcomeEmail(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            String name = payload.get("name");

            String content = String.format("<h1>Welcome to Cozy Creations, %s!</h1><p>We are glad to have you.</p>", name);
            emailService.sendEmail(email, "Welcome to Cozy Creations!", content);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
