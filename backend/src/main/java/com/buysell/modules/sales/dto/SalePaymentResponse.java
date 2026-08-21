package com.buysell.modules.sales.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

import com.buysell.modules.sales.enums.PaymentMode;
import com.buysell.modules.sales.enums.PaymentStatus;

@Data
public class SalePaymentResponse {
    private UUID id;
    private UUID saleTransactionId;
    private PaymentMode paymentMode;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String referenceNumber;
    private String idempotencyKey;
    private ZonedDateTime transactionTime;
    private UUID processedById;
    private ZonedDateTime createdAt;
}
