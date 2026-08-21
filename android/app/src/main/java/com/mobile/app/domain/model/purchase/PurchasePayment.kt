package com.mobile.app.domain.model.purchase

import java.math.BigDecimal
import java.time.LocalDateTime

data class PurchasePayment(
    val id: String,
    val amount: BigDecimal,
    val referenceNumber: String?,
    val paymentMode: PaymentMode,
    val status: PaymentStatus,
    val idempotencyKey: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
