
package com.hotel.model;

import lombok.Data;

@Data
public class PaymentRequest {
    private String method;
    private Double amount;
    private Long bookingId;

    // bKash
    private String bkashNumber;
    private String pin;

    // Card
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    // Bank
    private String bankName;
    private String accountNumber;
    private String accountHolder;
}