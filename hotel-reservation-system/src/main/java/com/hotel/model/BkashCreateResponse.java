package com.hotel.model;

import lombok.Data;

@Data
public class BkashCreateResponse {
    private String paymentID;
    private String bkashURL;
    private String statusMessage;
    private String merchantInvoiceNumber;
}
