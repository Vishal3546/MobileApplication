package com.buysell.modules.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePurchaseRequest {

    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @NotNull(message = "Device ID is required")
    private UUID deviceId;

    @DecimalMin(value = "0.0", inclusive = true, message = "Suggested price must be non-negative")
    private BigDecimal suggestedPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Negotiated price must be non-negative")
    private BigDecimal negotiatedPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Final price must be non-negative")
    private BigDecimal finalPrice;

    private String notes;
}
