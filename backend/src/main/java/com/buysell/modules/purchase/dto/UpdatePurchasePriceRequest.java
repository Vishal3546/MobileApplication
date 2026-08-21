package com.buysell.modules.purchase.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePurchasePriceRequest {

    @DecimalMin(value = "0.0", inclusive = true, message = "Suggested price must be non-negative")
    private BigDecimal suggestedPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Negotiated price must be non-negative")
    private BigDecimal negotiatedPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Final price must be non-negative")
    private BigDecimal finalPrice;

    private String notes;
}
