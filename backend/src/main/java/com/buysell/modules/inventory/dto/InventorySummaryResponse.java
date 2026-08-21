package com.buysell.modules.inventory.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class InventorySummaryResponse {
    private long totalStock;
    private long availableCount;
    private long reservedCount;
    private long inTransitCount;
    private long soldCount;
    private long returnedCount;
    private long damagedCount;
    private long blockedCount;
    private BigDecimal totalAcquisitionValue;
    private BigDecimal availableStockValue;
}
