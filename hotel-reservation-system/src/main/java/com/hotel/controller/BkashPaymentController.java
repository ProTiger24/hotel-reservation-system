package com.hotel.controller;

import com.hotel.service.BkashPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/bkash")
@CrossOrigin(origins = "*")
public class BkashPaymentController {
    
    @Autowired
    private BkashPaymentService bkashPaymentService;
    
    // 1. Create Agreement
    @PostMapping("/agreement/create")
    public Map<String, Object> createAgreement(@RequestBody Map<String, String> request) {
        String payerReference = request.get("payerReference");
        if (payerReference == null || payerReference.isEmpty()) {
            payerReference = "01704281617";
        }
        return bkashPaymentService.initiateAgreementFlow(payerReference);
    }
    
    // 2. Payment using Agreement
    @PostMapping("/agreement/payment")
    public Map<String, Object> paymentWithAgreement(@RequestBody Map<String, String> request) {
        String agreementId = request.get("agreementId");
        String amount = request.get("amount");
        
        if (agreementId == null || amount == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "agreementId and amount required");
            return error;
        }
        
        return bkashPaymentService.processPaymentWithAgreement(agreementId, amount);
    }
    
    // 3. Callback Handler
    @GetMapping("/callback")
    public Map<String, Object> handleCallback(@RequestParam Map<String, String> params) {
        System.out.println("📥 Callback received: " + params);
        
        String status = params.get("status");
        String agreementId = params.get("agreementId");
        String paymentId = params.get("paymentId");
        
        Map<String, Object> response = new HashMap<>();
        
        if ("success".equalsIgnoreCase(status)) {
            response.put("success", true);
            response.put("message", "Payment/Agreement successful!");
            response.put("agreementId", agreementId);
            response.put("paymentId", paymentId);
            System.out.println("✅ Success: Agreement " + agreementId + " completed");
        } else {
            response.put("success", false);
            response.put("message", "Status: " + status);
        }
        
        return response;
    }
    
    // 4. Test endpoint
    @GetMapping("/test")
    public Map<String, Object> testConnection() {
        String token = bkashPaymentService.getBkashToken();
        Map<String, Object> response = new HashMap<>();
        
        if (token != null && !token.startsWith("mock_token_")) {
            response.put("success", true);
            response.put("message", "bKash API connected!");
            response.put("token", token.substring(0, Math.min(token.length(), 30)) + "...");
        } else if (token != null && token.startsWith("mock_token_")) {
            response.put("success", true);
            response.put("message", "⚠️ Using MOCK bKash token (Sandbox not available)");
            response.put("isMock", true);
        } else {
            response.put("success", false);
            response.put("message", "Failed to connect to bKash API");
        }
        
        return response;
    }
}
