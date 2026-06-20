package com.hotel.service;

import com.hotel.config.BkashConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class BkashPaymentService {
    
    private final RestTemplate restTemplate;
    
    public BkashPaymentService() {
        this.restTemplate = new RestTemplate();
    }
    
    // Try Real Token, Fallback to Mock
    public String getBkashToken() {
        System.out.println("🔑 Attempting to get bKash token...");
        
        try {
            String url = BkashConfig.BKASH_SANDBOX_BASE_URL + "/token/grant";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("accept", "application/json");
            headers.set("username", BkashConfig.USERNAME);
            headers.set("password", BkashConfig.PASSWORD);
            
            Map<String, String> body = new HashMap<>();
            body.put("app_key", BkashConfig.APP_KEY);
            body.put("app_secret", BkashConfig.APP_SECRET);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String token = (String) response.getBody().get("id_token");
                if (token != null && !token.isEmpty()) {
                    System.out.println("✅ Real bKash token received!");
                    return token;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Real bKash API failed: " + e.getMessage());
        }
        
        // Fallback to Mock
        System.out.println("🔧 Using MOCK bKash token (Sandbox not available)");
        return "mock_token_" + UUID.randomUUID().toString();
    }
    
    // Create Agreement (Mock + Real)
    public Map<String, Object> initiateAgreementFlow(String payerReference) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String token = getBkashToken();
            
            // Check if Mock
            if (token.startsWith("mock_token_")) {
                return createMockAgreement(payerReference);
            }
            
            // Real API Call
            String url = BkashConfig.BKASH_SANDBOX_BASE_URL + "/agreement/create";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("X-APP-Key", BkashConfig.APP_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = new HashMap<>();
            body.put("payerReference", payerReference);
            body.put("callbackURL", BkashConfig.CALLBACK_URL);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> agreement = response.getBody();
                result.put("success", true);
                result.put("agreementId", agreement.get("agreementId"));
                result.put("paymentId", agreement.get("paymentId"));
                result.put("bkashURL", agreement.get("bkashURL"));
                result.put("status", agreement.get("agreementStatus"));
                result.put("message", "Agreement created successfully");
                result.put("isMock", false);
            } else {
                result.put("success", false);
                result.put("message", "Failed to create agreement");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        
        return result;
    }
    
    // Mock Agreement
    private Map<String, Object> createMockAgreement(String payerReference) {
        Map<String, Object> result = new HashMap<>();
        
        String mockAgreementId = "MOCK_AGR_" + System.currentTimeMillis();
        String mockPaymentId = "MOCK_PAY_" + System.currentTimeMillis();
        
        result.put("success", true);
        result.put("agreementId", mockAgreementId);
        result.put("paymentId", mockPaymentId);
        result.put("bkashURL", "https://sandbox.payment.bkash.com/?paymentId=" + mockPaymentId + "&mode=0000");
        result.put("status", "Initiated");
        result.put("message", "✅ Mock Agreement created! (Sandbox not available)");
        result.put("isMock", true);
        result.put("payerReference", payerReference);
        
        System.out.println("📝 Mock Agreement Created: " + mockAgreementId);
        return result;
    }
    
    // Payment with Agreement (Mock + Real)
    public Map<String, Object> processPaymentWithAgreement(String agreementId, String amount) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String token = getBkashToken();
            
            // Check if Mock
            if (token.startsWith("mock_token_")) {
                return createMockPayment(agreementId, amount);
            }
            
            // Real API Call
            String url = BkashConfig.BKASH_SANDBOX_BASE_URL + "/payment/create";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("X-APP-Key", BkashConfig.APP_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = new HashMap<>();
            body.put("agreementId", agreementId);
            body.put("amount", amount);
            body.put("currency", "BDT");
            body.put("merchantInvoiceNumber", "INV-" + System.currentTimeMillis());
            body.put("intent", "sale");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> payment = response.getBody();
                result.put("success", true);
                result.put("transactionId", payment.get("trxID"));
                result.put("paymentId", payment.get("paymentId"));
                result.put("amount", payment.get("amount"));
                result.put("status", payment.get("transactionStatus"));
                result.put("message", "Payment successful!");
                result.put("isMock", false);
            } else {
                result.put("success", false);
                result.put("message", "Payment failed");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }
        
        return result;
    }
    
    // Mock Payment
    private Map<String, Object> createMockPayment(String agreementId, String amount) {
        Map<String, Object> result = new HashMap<>();
        
        String mockTransactionId = "MOCK_TXN_" + System.currentTimeMillis();
        String mockPaymentId = "MOCK_PAY_" + System.currentTimeMillis();
        
        result.put("success", true);
        result.put("transactionId", mockTransactionId);
        result.put("paymentId", mockPaymentId);
        result.put("amount", amount);
        result.put("status", "Completed");
        result.put("message", "✅ Mock Payment successful! (Sandbox not available)");
        result.put("isMock", true);
        result.put("agreementId", agreementId);
        
        System.out.println("💳 Mock Payment: " + amount + " BDT using Agreement: " + agreementId);
        return result;
    }
}
