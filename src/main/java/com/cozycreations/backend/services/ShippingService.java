package com.cozycreations.backend.services;

import com.cozycreations.backend.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
public class ShippingService {

    @Value("${cozy.shiprocket.email}")
    private String email;

    @Value("${cozy.shiprocket.password}")
    private String password;

    private final RestClient restClient;
    private String token;
    private long tokenExpiryTime;

    public ShippingService() {
        this.restClient = RestClient.create("https://apiv2.shiprocket.in/v1/external");
    }

    private synchronized String getToken() {
        if (token != null && System.currentTimeMillis() < tokenExpiryTime) {
            return token;
        }
        
        try {
            Map<String, String> creds = Map.of("email", email, "password", password);
            Map response = restClient.post()
                    .uri("/auth/login")
                    .header("Content-Type", "application/json")
                    .body(creds)
                    .retrieve()
                    .body(Map.class);
            
            if (response != null && response.containsKey("token")) {
                this.token = (String) response.get("token");
                this.tokenExpiryTime = System.currentTimeMillis() + (9 * 24 * 60 * 60 * 1000L); // valid for ~10 days
                return this.token;
            }
        } catch (Exception e) {
            log.error("Failed to authenticate with Shiprocket", e);
        }
        return null;
    }

    public Map<String, Object> checkServiceability(String pincode, double weight, boolean isCod, double amount, int l, int w, int h) {
        String currentToken = getToken();
        if (currentToken == null) throw new RuntimeException("Shiprocket auth failed");

        try {
            String pickupPincode = "500055"; // from env typically
            String uri = String.format("/courier/serviceability?pickup_postcode=%s&delivery_postcode=%s&weight=%s&cod=%d&declared_value=%s&length=%d&breadth=%d&height=%d",
                    pickupPincode, pincode, weight, isCod ? 1 : 0, amount, l, w, h);
            
            return restClient.get()
                    .uri(uri)
                    .header("Authorization", "Bearer " + currentToken)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Shiprocket serviceability check failed", e);
            return new HashMap<>();
        }
    }

    public Map<String, Object> createShiprocketOrder(Order order) {
        String currentToken = getToken();
        if (currentToken == null) throw new RuntimeException("Shiprocket auth failed");

        // Logic to calculate dimensions and prepare payload...
        // For simplicity, returning a mock map indicating success if payload is sent.
        try {
            Map<String, Object> payload = new HashMap<>();
            // Map order properties to Shiprocket payload
            
            return restClient.post()
                    .uri("/orders/create/adhoc")
                    .header("Authorization", "Bearer " + currentToken)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.error("Failed to create Shiprocket order", e);
            throw e;
        }
    }
}
