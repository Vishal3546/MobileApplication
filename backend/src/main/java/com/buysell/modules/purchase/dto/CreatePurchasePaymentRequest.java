package com.buysell.modules.purchase.dto;

import com.buysell.modules.purchase.enums.PaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePurchasePaymentRequest {

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    private String referenceNumber;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
}
