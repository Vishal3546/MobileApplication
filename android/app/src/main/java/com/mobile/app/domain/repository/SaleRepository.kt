package com.mobile.app.domain.repository

import androidx.paging.PagingData
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.sales.SalePayment
import com.mobile.app.domain.model.sales.SaleTransaction
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.UUID

interface SaleRepository {
    fun getSalesPaging(
        status: String?,
        paymentStatus: String?,
        search: String?
    ): Flow<PagingData<SaleTransaction>>

    suspend fun getSaleById(id: UUID): NetworkState<SaleTransaction>
    suspend fun createSale(customerId: UUID, inventoryId: UUID, branchId: UUID): NetworkState<SaleTransaction>
    suspend fun overridePrice(id: UUID, newSellingPrice: BigDecimal, reason: String): NetworkState<SaleTransaction>
    suspend fun transitionSale(id: UUID, status: String): NetworkState<SaleTransaction>
    suspend fun createPayment(
        saleId: UUID,
        paymentMode: String,
        amount: BigDecimal,
        referenceNumber: String?,
        idempotencyKey: String
    ): NetworkState<SalePayment>
    suspend fun getPaymentsForSale(saleId: UUID): NetworkState<List<SalePayment>>
    suspend fun completeSale(id: UUID): NetworkState<SaleTransaction>
    suspend fun cancelSale(id: UUID, reason: String): NetworkState<SaleTransaction>
    suspend fun getSaleInvoicePdf(id: UUID): NetworkState<ByteArray>
}
