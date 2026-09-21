package com.buysell.modules.inventory.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandSummaryDto {
    private String brand;
    private Long count;
    private BigDecimal totalValue;
}
