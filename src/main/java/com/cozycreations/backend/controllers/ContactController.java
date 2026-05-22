package com.cozycreations.backend.controllers;

import com.cozycreations.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"https://cozycreations.in", "https://www.cozycreations.in", "http://localhost:3000", "http://localhost:5173"})
public class ContactController {

    @Autowired
    private EmailService emailService;

    @Value("${cozy.email.admin}")
    private String adminEmail;

    @PostMapping
    public ResponseEntity<?> submitContactForm(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String email = payload.get("email");
            String message = payload.get("message");
            String phone = payload.get("phone");

            String content = String.format("Name: %s<br>Email: %s<br>Phone: %s<br>Message: %s", name, email, phone, message);
            emailService.sendEmail(adminEmail, "New Contact Form Submission - " + name, content);

            return ResponseEntity.ok(Map.of("success", true, "message", "Contact form submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
