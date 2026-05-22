package com.cozycreations.backend.services;

import com.cozycreations.backend.models.Order;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WhatsappService {

    @Value("${cozy.twilio.whatsapp-number}")
    private String fromNumber;

    @Value("${cozy.twilio.admin-number}")
    private String adminNumber;

    @Value("${cozy.twilio.enable-notifications}")
    private boolean enableNotifications;

    private String normaliseIndianPhone(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        String digits = raw.replaceAll("[^\\d]", "");
        if (digits.length() == 10) return "+91" + digits;
        if (digits.length() == 12 && digits.startsWith("91")) return "+" + digits;
        if (digits.length() == 13 && digits.startsWith("091")) return "+" + digits.substring(1);
        String original = raw.trim();
        return original.startsWith("+") ? original : "+" + digits;
    }

    private void sendWhatsApp(String to, String body) {
        if (!enableNotifications) {
            log.info("ℹ️ WhatsApp Notification skipped (Feature disabled in config)");
            return;
        }
        if (to == null || fromNumber == null) return;
        String e164 = normaliseIndianPhone(to);
        if (e164 == null) return;

        String waNumber = e164.startsWith("whatsapp:") ? e164 : "whatsapp:" + e164;
        
        try {
            Message.creator(
                    new PhoneNumber(waNumber),
                    new PhoneNumber(fromNumber),
                    body
            ).create();
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}", waNumber, e);
        }
    }

    public void sendOrderConfirmationWhatsApp(String phone, Order orderData) {
        String name = (orderData.getCustomerName() != null) ? orderData.getCustomerName() : "Valued Customer";
        String orderId = (orderData.getId() != null) ? orderData.getId() : "—";
        String total = (orderData.getTotal() != null) ? String.valueOf(orderData.getTotal()) : "0";
        
        String body = """
                🕯️ *Cozy Creations*
                ──────────────────
                *Order Confirmed!* ✅
                
                Dear %s,
                
                Thank you for your order. We have successfully received it and our team is now preparing your handcrafted candles with love and care.
                
                *Order ID:* #%s
                *Order Total:* ₹%s
                
                We will notify you once your order has been delivered.
                
                Warm regards,
                *Cozy Creations* 🕯️
                """.formatted(name, orderId, total);

        sendWhatsApp(phone, body);
    }
}
