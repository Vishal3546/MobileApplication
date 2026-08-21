package com.buysell.modules.inventory.dto;

import com.buysell.modules.inventory.enums.InventoryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeInventoryStatusRequest {
    @NotNull(message = "Status is required")
    private InventoryStatus newStatus;
    
    private String reason;
}
