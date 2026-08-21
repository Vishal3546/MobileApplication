package com.buysell.modules.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class StockTransferRequest {
    @NotNull(message = "Destination branch is required")
    private UUID toBranchId;

    @NotEmpty(message = "At least one inventory item is required")
    private List<UUID> inventoryItemIds;

    private String notes;
}
