package com.buysell.modules.inventory.dto;

import com.buysell.modules.inventory.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
public class InventoryStatusHistoryResponse {
    private UUID id;
    private UUID inventoryItemId;
    private InventoryStatus previousStatus;
    private InventoryStatus newStatus;
    private String reason;
    private String referenceType;
    private UUID referenceId;
    private String performedByUsername;
    private UUID branchId;
    private ZonedDateTime createdAt;
}
