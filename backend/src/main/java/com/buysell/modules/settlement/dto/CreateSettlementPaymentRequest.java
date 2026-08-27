package com.buysell.modules.settlement.dto;

import com.buysell.modules.settlement.enums.SettlementPaymentMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSettlementPaymentRequest {

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull
    private SettlementPaymentMode paymentMode;
    private String referenceNumber;

    @NotBlank
    private String idempotencyKey;
    public BigDecimal getAmount() {
        return this.amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public SettlementPaymentMode getPaymentMode() {
        return this.paymentMode;
    }
    public void setPaymentMode(SettlementPaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }
    public String getReferenceNumber() {
        return this.referenceNumber;
    }
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }
    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
