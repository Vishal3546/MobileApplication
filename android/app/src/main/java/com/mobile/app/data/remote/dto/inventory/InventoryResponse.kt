package com.mobile.app.data.remote.dto.inventory

import java.math.BigDecimal
import java.util.UUID

data class InventoryResponse(
    val id: UUID,
    val stockCode: String,
    val branchId: UUID,
    val deviceId: UUID,
    val purchaseId: UUID?,
    val status: String,
    val costPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val createdBy: String,
    val createdAt: String,
    val reservedBy: String?,
    val reservedUntil: String?,
    val deviceSummary: DeviceSummaryDto?
)

data class DeviceSummaryDto(
    val brand: String,
    val model: String,
    val variant: String?,
    val color: String?,
    val ram: String?,
    val storage: String?,
    val imei: String
)

data class InventorySummaryResponse(
    val totalStock: Int,
    val availableStock: Int,
    val reservedStock: Int,
    val inTransitStock: Int,
    val soldStock: Int,
    val returnedStock: Int,
    val damagedStock: Int,
    val blockedStock: Int,
    val acquisitionValue: BigDecimal,
    val availableStockValue: BigDecimal,
    val listedSellingValue: BigDecimal
)

data class InventoryStatusHistoryResponse(
    val id: UUID,
    val inventoryId: UUID,
    val previousStatus: String?,
    val newStatus: String,
    val reason: String?,
    val performedBy: String,
    val createdAt: String
)

data class StockTransferRequest(
    val inventoryIds: List<UUID>,
    val sourceBranchId: UUID,
    val destinationBranchId: UUID,
    val reason: String?
)

data class StockTransferResponse(
    val id: UUID,
    val transferNumber: String,
    val sourceBranchId: UUID,
    val destinationBranchId: UUID,
    val status: String,
    val requestedBy: String,
    val requestedAt: String,
    val approvedBy: String?,
    val approvedAt: String?,
    val completedAt: String?,
    val inventoryIds: List<UUID>
)
