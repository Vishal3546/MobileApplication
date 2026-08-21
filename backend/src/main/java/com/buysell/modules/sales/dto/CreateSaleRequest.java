package com.buysell.modules.sales.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSaleRequest {
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Inventory Item ID is required")
    private UUID inventoryItemId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price must be positive")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount amount must be positive")
    private BigDecimal discountAmount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be positive")
    private BigDecimal taxAmount;

    private String notes;
}
