package com.buysell.modules.purchase.dto;

import com.buysell.modules.purchase.enums.TransactionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PurchaseResponse {
    private UUID id;
    private String purchaseNumber;
    private UUID customerId;
    private UUID deviceId;
    private UUID employeeId;
    private UUID branchId;
    private BigDecimal suggestedPrice;
    private BigDecimal negotiatedPrice;
    private BigDecimal finalPrice;
    private String notes;
    private TransactionStatus transactionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
