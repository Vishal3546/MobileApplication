package com.buysell.modules.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StockTransferItemResponse {
    private UUID id;
    private UUID inventoryItemId;
    private String stockCode;
}
