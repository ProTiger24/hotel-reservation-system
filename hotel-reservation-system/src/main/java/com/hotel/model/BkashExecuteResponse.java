package com.hotel.model;

import lombok.Data;

@Data
public class BkashExecuteResponse {
    private String paymentID;
    private String trxID;
    private String transactionStatus;
    private String amount;
    private String currency;
    private String merchantInvoiceNumber;
    private String statusMessage;
}
