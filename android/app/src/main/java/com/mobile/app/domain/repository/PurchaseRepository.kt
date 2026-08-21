package com.mobile.app.domain.repository

import com.mobile.app.domain.model.purchase.Purchase
import com.mobile.app.domain.model.purchase.PurchasePayment
import com.mobile.app.domain.model.purchase.PurchaseStatus
import java.math.BigDecimal
import okhttp3.ResponseBody
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface PurchaseRepository {
    suspend fun createPurchase(customerId: String, deviceId: String, suggestedPrice: BigDecimal, negotiatedPrice: BigDecimal, finalPrice: BigDecimal, notes: String?): Result<Purchase>
    suspend fun getPurchase(id: String): Result<Purchase>
    fun getPurchasesPaging(search: String?, status: String?, startDate: String?, endDate: String?): Flow<PagingData<Purchase>>
    suspend fun getPurchases(page: Int, size: Int, search: String?, status: String?, startDate: String?, endDate: String?): Result<Pair<List<Purchase>, Int>>
    suspend fun transitionPurchase(id: String, status: PurchaseStatus, notes: String?): Result<Purchase>
    suspend fun createPayment(id: String, amount: BigDecimal, referenceNumber: String?, paymentMode: String, idempotencyKey: String): Result<PurchasePayment>
    suspend fun getPayments(id: String): Result<List<PurchasePayment>>
    suspend fun completePurchase(id: String): Result<Purchase>
    suspend fun cancelPurchase(id: String, reason: String): Result<Purchase>
    suspend fun getReceipt(id: String): Result<ResponseBody>
}
