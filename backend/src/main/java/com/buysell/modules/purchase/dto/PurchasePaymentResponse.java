package com.buysell.modules.purchase.dto;

import com.buysell.modules.purchase.enums.PaymentMode;
import com.buysell.modules.purchase.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PurchasePaymentResponse {
    private UUID id;
    private UUID purchaseTransactionId;
    private PaymentMode paymentMode;
    private BigDecimal amount;
    private String referenceNumber;
    private PaymentStatus paymentStatus;
    private LocalDateTime transactionTime;
    private String idempotencyKey;
    private UUID processedBy;
    private LocalDateTime createdAt;
}
