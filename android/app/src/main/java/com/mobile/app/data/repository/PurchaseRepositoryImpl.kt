package com.mobile.app.data.repository

import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.mapper.toDto
import com.mobile.app.data.remote.api.PurchaseApi
import com.mobile.app.data.remote.dto.purchase.*
import com.mobile.app.domain.model.purchase.Purchase
import com.mobile.app.domain.model.purchase.PurchasePayment
import com.mobile.app.domain.model.purchase.PurchaseStatus
import com.mobile.app.domain.repository.PurchaseRepository
import java.math.BigDecimal
import okhttp3.ResponseBody
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class PurchaseRepositoryImpl(
    private val api: PurchaseApi
) : PurchaseRepository {
    override suspend fun createPurchase(customerId: String, deviceId: String, suggestedPrice: BigDecimal, negotiatedPrice: BigDecimal, finalPrice: BigDecimal, notes: String?): Result<Purchase> {
        return try {
            val response = api.createPurchase(PurchaseCreateDto(customerId, deviceId, suggestedPrice, negotiatedPrice, finalPrice, notes))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPurchase(id: String): Result<Purchase> {
        return try {
            val response = api.getPurchase(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    
    override fun getPurchasesPaging(search: String?, status: String?, startDate: String?, endDate: String?): Flow<PagingData<Purchase>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { PurchasePagingSource(api, search, status, startDate, endDate) }
        ).flow
    }

    override suspend fun getPurchases(page: Int, size: Int, search: String?, status: String?, startDate: String?, endDate: String?): Result<Pair<List<Purchase>, Int>> {
        return try {
            val response = api.getPurchases(page, size, search, status, startDate, endDate)
            Result.success(Pair(response.content.map { it.toDomain() }, response.totalPages))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun transitionPurchase(id: String, status: PurchaseStatus, notes: String?): Result<Purchase> {
        return try {
            val response = api.transitionPurchase(id, PurchaseTransitionDto(status.toDto(), notes))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPayment(id: String, amount: BigDecimal, referenceNumber: String?, paymentMode: String, idempotencyKey: String): Result<PurchasePayment> {
        return try {
            val response = api.createPayment(id, PurchasePaymentCreateDto(amount, referenceNumber, PaymentModeDto.valueOf(paymentMode), idempotencyKey))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPayments(id: String): Result<List<PurchasePayment>> {
        return try {
            val response = api.getPayments(id)
            Result.success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun completePurchase(id: String): Result<Purchase> {
        return try {
            val response = api.completePurchase(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelPurchase(id: String, reason: String): Result<Purchase> {
        return try {
            val response = api.cancelPurchase(id, PurchaseCancelDto(reason))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReceipt(id: String): Result<ResponseBody> {
        return try {
            val response = api.getReceipt(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to download receipt"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
