package com.buysell.modules.inventory.dto;

import com.buysell.modules.inventory.enums.TransferStatus;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StockTransferResponse {
    private UUID id;
    private String transferNumber;
    private UUID fromBranchId;
    private UUID toBranchId;
    private TransferStatus status;
    private String requestedByUsername;
    private String approvedByUsername;
    private ZonedDateTime requestedAt;
    private ZonedDateTime approvedAt;
    private ZonedDateTime completedAt;
    private String notes;
    private List<StockTransferItemResponse> items;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
