package com.buysell.modules.settlement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateReconciliationRequest {

    @NotNull
    private UUID shopId;

    @NotNull
    private BigDecimal actualAmount;
    private String notes;
    public UUID getShopId() {
        return this.shopId;
    }
    public void setShopId(UUID shopId) {
        this.shopId = shopId;
    }
    public BigDecimal getActualAmount() {
        return this.actualAmount;
    }
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
