package com.mobile.app.domain.model.sales

import com.mobile.app.domain.model.inventory.Inventory
import java.math.BigDecimal
import java.util.UUID

data class SaleTransaction(
    val id: UUID,
    val saleNumber: String,
    val customerId: UUID,
    val inventoryItem: Inventory?,
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

data class SalePayment(
    val id: UUID,
    val saleTransactionId: UUID,
    val paymentMode: String,
    val amount: BigDecimal,
    val referenceNumber: String?,
    val status: String,
    val createdAt: String
)
