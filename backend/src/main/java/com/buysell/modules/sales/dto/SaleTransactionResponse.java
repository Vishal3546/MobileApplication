package com.buysell.modules.sales.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Data;

import com.buysell.modules.sales.enums.PaymentStatus;
import com.buysell.modules.sales.enums.SaleStatus;

@Data
public class SaleTransactionResponse {
    private UUID id;
    private String saleNumber;
    private UUID customerId;
    private UUID inventoryItemId;
    private UUID employeeId;
    private UUID branchId;
    private BigDecimal sellingPrice;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal finalAmount;
    private String currency;
    private SaleStatus saleStatus;
    private PaymentStatus paymentStatus;
    private ZonedDateTime warrantyStartDate;
    private ZonedDateTime warrantyEndDate;
    private String returnPolicyCode;
    private String notes;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
