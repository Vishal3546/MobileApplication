package com.buysell.modules.inventory.dto;

import com.buysell.modules.inventory.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
public class InventoryResponse {
    private UUID id;
    private String stockCode;
    private UUID deviceId;
    private UUID purchaseTransactionId;
    private UUID branchId;
    private InventoryStatus status;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private ZonedDateTime reservedUntil;
    private String reservedByUsername;
    private String conditionSummary;
    private String notes;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
