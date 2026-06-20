package com.hotel.controller;

import com.hotel.model.PaymentRequest;
import com.hotel.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping("/process")
    public Map<String, Object> processPayment(@RequestBody PaymentRequest request) {
        switch (request.getMethod().toUpperCase()) {
            case "BKASH":
                return paymentService.processBkashPayment(request);
            case "CARD":
                return paymentService.processCardPayment(request);
            case "BANK":
                return paymentService.processBankPayment(request);
            default:
                return Map.of(
                    "success", false,
                    "message", "❌ Unsupported payment method"
                );
        }
    }
}
