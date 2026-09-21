package com.buysell.modules.inventory.dto;

import java.math.BigDecimal;

public interface BrandSummaryProjection {
    String getBrand();
    Long getCount();
    BigDecimal getTotalValue();
}
