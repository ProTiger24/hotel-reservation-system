package com.hotel.model;

import lombok.Data;

@Data
public class BkashPaymentRequest {
    private String mode = "0011";
    private String amount;
    private String currency = "BDT";
    private String intent = "sale";
    private String merchantInvoiceNumber;
    private String payerReference;
    private String callbackURL;
}
