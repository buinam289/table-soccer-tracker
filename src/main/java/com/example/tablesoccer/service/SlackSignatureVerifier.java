package com.example.tablesoccer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class SlackSignatureVerifier {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String VERSION = "v0";
    private static final long MAX_REQUEST_AGE_IN_SECONDS = 300; // 5 minutes timeout

    @Value("${slack.signing.secret}")
    private String slackSigningSecret;

    /**
     * Verify that the request actually came from Slack
     * 
     * @param timestamp The X-Slack-Request-Timestamp header value
     * @param signature The X-Slack-Signature header value
     * @param requestBody The raw request body
     * @return true if the request signature is valid, false otherwise
     */
    public boolean isValidRequest(String timestamp, String signature, String requestBody) {
        if (timestamp == null || signature == null || requestBody == null) {
            return false;
        }

        // Check if the timestamp is too old
        long currentTimeInSeconds = Instant.now().getEpochSecond();
        long requestTimestamp = Long.parseLong(timestamp);
        
        if (Math.abs(currentTimeInSeconds - requestTimestamp) > MAX_REQUEST_AGE_IN_SECONDS) {
            return false; // Request is too old, might be a replay attack
        }

        // Calculate expected signature
        try {
            String baseString = VERSION + ":" + timestamp + ":" + requestBody;
            
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec key = new SecretKeySpec(slackSigningSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256_ALGORITHM);
            mac.init(key);
            
            byte[] signatureBytes = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = VERSION + "=" + HexFormat.of().formatHex(signatureBytes);
            
            return calculatedSignature.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }
}