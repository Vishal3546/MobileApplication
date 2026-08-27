package com.buysell.modules.settlement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopLedgerSummaryResponse {
    private BigDecimal totalReceivable;
    private BigDecimal totalPayable;
    private BigDecimal netBalance;
    public BigDecimal getTotalReceivable() {
        return this.totalReceivable;
    }
    public void setTotalReceivable(BigDecimal totalReceivable) {
        this.totalReceivable = totalReceivable;
    }
    public BigDecimal getTotalPayable() {
        return this.totalPayable;
    }
    public void setTotalPayable(BigDecimal totalPayable) {
        this.totalPayable = totalPayable;
    }
    public BigDecimal getNetBalance() {
        return this.netBalance;
    }
    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }
}
