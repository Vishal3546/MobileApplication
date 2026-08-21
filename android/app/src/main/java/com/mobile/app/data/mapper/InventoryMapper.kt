package com.mobile.app.data.mapper

import com.mobile.app.data.remote.dto.inventory.InventoryResponse
import com.mobile.app.data.remote.dto.inventory.InventoryStatusHistoryResponse
import com.mobile.app.data.remote.dto.inventory.InventorySummaryResponse
import com.mobile.app.data.remote.dto.inventory.StockTransferResponse
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.model.inventory.InventoryStatusHistory
import com.mobile.app.domain.model.inventory.InventorySummary
import com.mobile.app.domain.model.inventory.StockTransfer

fun InventoryResponse.toDomain(): Inventory {
    return Inventory(
        id = id,
        stockCode = stockCode,
        branchId = branchId,
        deviceId = deviceId,
        purchaseId = purchaseId,
        status = status,
        costPrice = costPrice,
        sellingPrice = sellingPrice,
        createdBy = createdBy,
        createdAt = createdAt,
        reservedBy = reservedBy,
        reservedUntil = reservedUntil,
        brand = deviceSummary?.brand ?: "Unknown",
        model = deviceSummary?.model ?: "Unknown",
        variant = deviceSummary?.variant ?: "",
        color = deviceSummary?.color ?: "",
        ram = deviceSummary?.ram ?: "",
        storage = deviceSummary?.storage ?: "",
        imei = deviceSummary?.imei ?: "Unknown"
    )
}

fun InventorySummaryResponse.toDomain(): InventorySummary {
    return InventorySummary(
        totalStock = totalStock,
        availableStock = availableStock,
        reservedStock = reservedStock,
        inTransitStock = inTransitStock,
        soldStock = soldStock,
        returnedStock = returnedStock,
        damagedStock = damagedStock,
        blockedStock = blockedStock,
        acquisitionValue = acquisitionValue,
        availableStockValue = availableStockValue,
        listedSellingValue = listedSellingValue
    )
}

fun InventoryStatusHistoryResponse.toDomain(): InventoryStatusHistory {
    return InventoryStatusHistory(
        id = id,
        inventoryId = inventoryId,
        previousStatus = previousStatus,
        newStatus = newStatus,
        reason = reason,
        performedBy = performedBy,
        createdAt = createdAt
    )
}

fun StockTransferResponse.toDomain(): StockTransfer {
    return StockTransfer(
        id = id,
        transferNumber = transferNumber,
        sourceBranchId = sourceBranchId,
        destinationBranchId = destinationBranchId,
        status = status,
        requestedBy = requestedBy,
        requestedAt = requestedAt,
        approvedBy = approvedBy,
        approvedAt = approvedAt,
        completedAt = completedAt,
        inventoryIds = inventoryIds
    )
}
