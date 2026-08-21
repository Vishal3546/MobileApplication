package com.mobile.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mobile.app.domain.model.safeApiCall
import com.mobile.app.domain.model.map
import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.*
import com.mobile.app.data.remote.dto.inventory.StockTransferRequest
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.model.inventory.InventoryStatusHistory
import com.mobile.app.domain.model.inventory.InventorySummary
import com.mobile.app.domain.model.inventory.StockTransfer
import com.mobile.app.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val inventoryApi: InventoryApi,
    private val stockTransferApi: StockTransferApi
) : InventoryRepository {

    override fun getInventoryListPaging(
        status: String?,
        search: String?,
        branchId: UUID?
    ): Flow<PagingData<Inventory>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { InventoryPagingSource(inventoryApi, status, search, branchId) }
        ).flow
    }

    override suspend fun getInventoryById(id: UUID): NetworkState<Inventory> {
        return safeApiCall { inventoryApi.getInventoryById(id) }.map { it.toDomain() }
    }

    override suspend fun getInventorySummary(branchId: UUID?): NetworkState<InventorySummary> {
        return safeApiCall { inventoryApi.getInventorySummary(branchId) }.map { it.toDomain() }
    }

    override suspend fun changeStatus(id: UUID, status: String, reason: String?): NetworkState<Inventory> {
        return safeApiCall { inventoryApi.changeStatus(id, ChangeInventoryStatusRequest(status, reason)) }.map { it.toDomain() }
    }

    override suspend fun updateSellingPrice(id: UUID, sellingPrice: BigDecimal, reason: String?): NetworkState<Inventory> {
        return safeApiCall { inventoryApi.updateSellingPrice(id, UpdateSellingPriceRequest(sellingPrice, reason)) }.map { it.toDomain() }
    }

    override suspend fun reserveInventory(id: UUID, customerId: UUID?, reason: String?): NetworkState<Inventory> {
        return safeApiCall { inventoryApi.reserveInventory(id, ReserveInventoryRequest(customerId, reason)) }.map { it.toDomain() }
    }

    override suspend fun releaseInventory(id: UUID): NetworkState<Inventory> {
        return safeApiCall { inventoryApi.releaseInventory(id) }.map { it.toDomain() }
    }

    override suspend fun getInventoryHistory(id: UUID): NetworkState<List<InventoryStatusHistory>> {
        return safeApiCall { inventoryApi.getInventoryHistory(id) }.map { response -> response.map { it.toDomain() } }
    }

    override fun getStockTransfersPaging(
        status: String?,
        branchId: UUID?
    ): Flow<PagingData<StockTransfer>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { StockTransferPagingSource(stockTransferApi, status, branchId) }
        ).flow
    }

    override suspend fun createTransfer(inventoryIds: List<UUID>, sourceBranchId: UUID, destinationBranchId: UUID, reason: String?): NetworkState<StockTransfer> {
        val request = StockTransferRequest(inventoryIds, sourceBranchId, destinationBranchId, reason)
        return safeApiCall { stockTransferApi.createTransfer(request) }.map { it.toDomain() }
    }

    override suspend fun getTransferById(id: UUID): NetworkState<StockTransfer> {
        return safeApiCall { stockTransferApi.getTransferById(id) }.map { it.toDomain() }
    }

    override suspend fun transitionTransfer(id: UUID, status: String): NetworkState<StockTransfer> {
        return safeApiCall { stockTransferApi.transitionTransfer(id, status) }.map { it.toDomain() }
    }

    override suspend fun completeTransfer(id: UUID): NetworkState<StockTransfer> {
        return safeApiCall { stockTransferApi.completeTransfer(id) }.map { it.toDomain() }
    }
}
