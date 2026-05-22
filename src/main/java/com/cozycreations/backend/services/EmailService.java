package com.cozycreations.backend.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.List;

@Slf4j
@Service
public class EmailService {

    @Value("${cozy.resend.api-key}")
    private String resendApiKey;

    @Value("${cozy.email.from}")
    private String emailFrom;

    private final RestClient restClient;

    public EmailService() {
        this.restClient = RestClient.create("https://api.resend.com");
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        if (resendApiKey == null || resendApiKey.isEmpty()) {
            log.warn("Resend API Key is missing. Skipping email send.");
            return;
        }

        try {
            Map<String, Object> payload = Map.of(
                    "from", "Cozy Creations <" + emailFrom + ">",
                    "to", List.of(to),
                    "subject", subject,
                    "html", htmlContent
            );

            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
                    
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}
