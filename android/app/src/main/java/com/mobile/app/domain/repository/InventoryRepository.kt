package com.mobile.app.domain.repository

import androidx.paging.PagingData
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.model.inventory.InventoryStatusHistory
import com.mobile.app.domain.model.inventory.InventorySummary
import com.mobile.app.domain.model.inventory.StockTransfer
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.UUID

interface InventoryRepository {
    fun getInventoryListPaging(
        status: String?,
        search: String?,
        branchId: UUID?
    ): Flow<PagingData<Inventory>>

    suspend fun getInventoryById(id: UUID): NetworkState<Inventory>
    suspend fun getInventorySummary(branchId: UUID?): NetworkState<InventorySummary>
    suspend fun changeStatus(id: UUID, status: String, reason: String?): NetworkState<Inventory>
    suspend fun updateSellingPrice(id: UUID, sellingPrice: BigDecimal, reason: String?): NetworkState<Inventory>
    suspend fun reserveInventory(id: UUID, customerId: UUID?, reason: String?): NetworkState<Inventory>
    suspend fun releaseInventory(id: UUID): NetworkState<Inventory>
    suspend fun getInventoryHistory(id: UUID): NetworkState<List<InventoryStatusHistory>>

    fun getStockTransfersPaging(
        status: String?,
        branchId: UUID?
    ): Flow<PagingData<StockTransfer>>

    suspend fun createTransfer(inventoryIds: List<UUID>, sourceBranchId: UUID, destinationBranchId: UUID, reason: String?): NetworkState<StockTransfer>
    suspend fun getTransferById(id: UUID): NetworkState<StockTransfer>
    suspend fun transitionTransfer(id: UUID, status: String): NetworkState<StockTransfer>
    suspend fun completeTransfer(id: UUID): NetworkState<StockTransfer>
}
