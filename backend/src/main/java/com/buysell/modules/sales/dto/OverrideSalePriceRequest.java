package com.buysell.modules.sales.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OverrideSalePriceRequest {
    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price must be positive")
    private BigDecimal sellingPrice;
    
    private String notes;
}
