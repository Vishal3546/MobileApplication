package com.mobile.app.domain.model.inventory

import java.math.BigDecimal
import java.util.UUID

data class Inventory(
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
    val brand: String,
    val model: String,
    val variant: String,
    val color: String,
    val ram: String,
    val storage: String,
    val imei: String
)

data class InventorySummary(
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

data class InventoryStatusHistory(
    val id: UUID,
    val inventoryId: UUID,
    val previousStatus: String?,
    val newStatus: String,
    val reason: String?,
    val performedBy: String,
    val createdAt: String
)

data class StockTransfer(
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
