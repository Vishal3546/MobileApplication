package com.mobile.app.data.remote.dto.sales

import java.math.BigDecimal
import java.util.UUID
import com.mobile.app.data.remote.dto.inventory.InventoryResponse

data class SaleTransactionResponse(
    val id: UUID,
    val saleNumber: String,
    val customerId: UUID,
    val inventoryItem: InventoryResponse?,
    val branchId: UUID,
    val sellingPrice: BigDecimal,
    val discount: BigDecimal,
    val tax: BigDecimal,
    val finalAmount: BigDecimal,
    val paymentStatus: String,
    val saleStatus: String,
    val warrantyStart: String?,
    val warrantyEnd: String?,
    val createdBy: String,
    val createdAt: String,
    val completedAt: String?
)

data class SalePaymentResponse(
    val id: UUID,
    val saleTransactionId: UUID,
    val paymentMode: String,
    val amount: BigDecimal,
    val referenceNumber: String?,
    val status: String,
    val createdAt: String
)

data class CreateSaleRequest(
    val customerId: UUID,
    val inventoryId: UUID,
    val branchId: UUID
)

data class OverrideSalePriceRequest(
    val newSellingPrice: BigDecimal,
    val reason: String
)

data class CreateSalePaymentRequest(
    val saleTransactionId: UUID,
    val paymentMode: String,
    val amount: BigDecimal,
    val referenceNumber: String?,
    val idempotencyKey: String
)
