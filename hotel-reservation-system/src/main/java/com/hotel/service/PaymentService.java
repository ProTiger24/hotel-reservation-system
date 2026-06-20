package com.hotel.service;

import com.hotel.model.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PaymentService {
    
    @Autowired
    private BkashPaymentService bkashPaymentService;
    
    public Map<String, Object> processBkashPayment(PaymentRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Generate payer reference
            String payerReference = "01957123090"; // Default sandbox wallet
            
            // Initiate agreement flow
            Map<String, Object> agreementResult = bkashPaymentService.initiateAgreementFlow(payerReference);
            
            if ((boolean) agreementResult.get("success")) {
                response.put("success", true);
                response.put("message", "✅ bKash payment initiated!");
                response.put("bkashURL", agreementResult.get("bkashURL"));
                response.put("agreementId", agreementResult.get("agreementId"));
                response.put("paymentId", agreementResult.get("paymentId"));
                response.put("status", "PENDING");
            } else {
                response.put("success", false);
                response.put("message", agreementResult.get("message"));
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return response;
    }
    
    public Map<String, Object> processCardPayment(PaymentRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("paymentMethod", "CARD");
        response.put("amount", request.getAmount());
        
        if (request.getCardNumber() == null || request.getCardNumber().length() < 15) {
            response.put("success", false);
            response.put("message", "❌ Invalid card number");
            return response;
        }
        
        if (request.getCvv() == null || request.getCvv().length() < 3) {
            response.put("success", false);
            response.put("message", "❌ Invalid CVV");
            return response;
        }
        
        try {
            Thread.sleep(1500);
            String transactionId = "CARD-" + System.currentTimeMillis();
            response.put("success", true);
            response.put("message", "✅ Card payment successful!");
            response.put("transactionId", transactionId);
            response.put("status", "COMPLETED");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "❌ Payment failed");
        }
        return response;
    }
    
    public Map<String, Object> processBankPayment(PaymentRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("paymentMethod", "BANK");
        response.put("amount", request.getAmount());
        
        if (request.getBankName() == null || request.getBankName().isEmpty()) {
            response.put("success", false);
            response.put("message", "❌ Bank name required");
            return response;
        }
        
        try {
            Thread.sleep(1500);
            String transactionId = "BANK-" + System.currentTimeMillis();
            response.put("success", true);
            response.put("message", "✅ Bank transfer initiated!");
            response.put("transactionId", transactionId);
            response.put("status", "PENDING");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "❌ Transfer failed");
        }
        return response;
    }
}
