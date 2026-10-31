package com.yourhotel.booking.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class BkashService {

    @Value("${bkash.base_url}") private String baseUrl;
    @Value("${bkash.username}") private String username;
    @Value("${bkash.password}") private String password;
    @Value("${bkash.app_key}") private String appKey;
    @Value("${bkash.app_secret}") private String appSecret;
    @Value("${bkash.callback_url}") private String callbackUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String grantToken() {
        String url = baseUrl + "/tokenized/checkout/token/grant";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("username", username);
        headers.set("password", password);
        Map<String, String> body = Map.of("app_key", appKey, "app_secret", appSecret);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return (String) response.getBody().get("id_token");
    }

    // 🚀 Updated method signature to include bookingId
    public Map<String, Object> createPayment(String token, String amount, String invoiceNumber, String payerReference, Long bookingId, String type) {
        String url = baseUrl + "/tokenized/checkout/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        headers.set("X-APP-Key", appKey);
        Map<String, Object> body = new HashMap<>();
        body.put("mode", "0011");
        body.put("payerReference", payerReference);
        // 🚀 Append bookingId to the callback URL
        body.put("callbackURL", callbackUrl + "?bookingId=" + bookingId + "&type=" + type);
        body.put("amount", amount);
        body.put("currency", "BDT");
        body.put("intent", "sale");
        body.put("merchantInvoiceNumber", invoiceNumber);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return response.getBody();
    }

    public Map<String, Object> executePayment(String token, String paymentID) {
        String url = baseUrl + "/tokenized/checkout/execute";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        headers.set("X-APP-Key", appKey);
        Map<String, String> body = Map.of("paymentID", paymentID);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return response.getBody();
    }
}
