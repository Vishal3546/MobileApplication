package com.mobile.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mobile.app.domain.model.safeApiCall
import com.mobile.app.domain.model.map
import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.SaleApi
import com.mobile.app.data.remote.dto.sales.CreateSalePaymentRequest
import com.mobile.app.data.remote.dto.sales.CreateSaleRequest
import com.mobile.app.data.remote.dto.sales.OverrideSalePriceRequest
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.sales.SalePayment
import com.mobile.app.domain.model.sales.SaleTransaction
import com.mobile.app.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleRepositoryImpl @Inject constructor(
    private val saleApi: SaleApi
) : SaleRepository {

    override fun getSalesPaging(
        status: String?,
        paymentStatus: String?,
        search: String?
    ): Flow<PagingData<SaleTransaction>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SalePagingSource(saleApi, status, paymentStatus, search) }
        ).flow
    }

    override suspend fun getSaleById(id: UUID): NetworkState<SaleTransaction> {
        return safeApiCall { saleApi.getSaleById(id) }.map { it.toDomain() }
    }

    override suspend fun createSale(customerId: UUID, inventoryId: UUID, branchId: UUID): NetworkState<SaleTransaction> {
        return safeApiCall { saleApi.createSale(CreateSaleRequest(customerId, inventoryId, branchId)) }.map { it.data.toDomain() }
    }

    override suspend fun overridePrice(id: UUID, newSellingPrice: BigDecimal, reason: String): NetworkState<SaleTransaction> {
        return safeApiCall { saleApi.overridePrice(id, OverrideSalePriceRequest(newSellingPrice, reason)) }.map { it.data.toDomain() }
    }

    override suspend fun transitionSale(id: UUID, status: String): NetworkState<SaleTransaction> {
        return safeApiCall { saleApi.transitionSale(id, status) }.map { it.data.toDomain() }
    }

    override suspend fun createPayment(
        saleId: UUID,
        paymentMode: String,
        amount: BigDecimal,
        referenceNumber: String?,
        idempotencyKey: String
    ): NetworkState<SalePayment> {
        val request = CreateSalePaymentRequest(saleId, paymentMode, amount, referenceNumber, idempotencyKey)
        return safeApiCall { saleApi.createPayment(request) }.map { it.data.toDomain() }
    }

    override suspend fun getPaymentsForSale(saleId: UUID): NetworkState<List<SalePayment>> {
        return safeApiCall { saleApi.getPaymentsForSale(saleId) }.map { response -> response.data.map { it.toDomain() } }
    }

    override suspend fun completeSale(id: UUID): NetworkState<SaleTransaction> {
        return safeApiCall { saleApi.completeSale(id) }.map { it.data.toDomain() }
    }

    override suspend fun cancelSale(id: UUID, reason: String): NetworkState<SaleTransaction> {
        return safeApiCall { saleApi.cancelSale(id, reason) }.map { it.data.toDomain() }
    }

    override suspend fun getSaleInvoicePdf(id: UUID): NetworkState<ByteArray> {
        return safeApiCall { saleApi.getSaleInvoice(id) }.map { it.bytes() }
    }
}
