package com.cozycreations.backend.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${cozy.twilio.account-sid}")
    private String accountSid;

    @Value("${cozy.twilio.auth-token}")
    private String authToken;

    @PostConstruct
    public void initTwilio() {
        if (accountSid != null && !accountSid.isEmpty() && authToken != null && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
        }
    }
}
