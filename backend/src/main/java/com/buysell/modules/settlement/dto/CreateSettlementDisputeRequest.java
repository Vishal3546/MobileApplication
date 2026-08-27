package com.buysell.modules.settlement.dto;

import com.buysell.modules.settlement.enums.DisputeReason;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSettlementDisputeRequest {

    @NotNull
    private DisputeReason reason;
    private BigDecimal claimedAmount;
    public DisputeReason getReason() {
        return this.reason;
    }
    public void setReason(DisputeReason reason) {
        this.reason = reason;
    }
    public BigDecimal getClaimedAmount() {
        return this.claimedAmount;
    }
    public void setClaimedAmount(BigDecimal claimedAmount) {
        this.claimedAmount = claimedAmount;
    }
}
