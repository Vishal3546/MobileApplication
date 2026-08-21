package com.buysell.modules.sales.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import com.buysell.modules.sales.enums.PaymentMode;

@Data
public class CreateSalePaymentRequest {
    @NotNull(message = "Sale transaction ID is required")
    private UUID saleTransactionId;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private String referenceNumber;

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
}
