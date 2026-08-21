package com.mobile.app.domain.model.purchase

import com.mobile.app.domain.model.Customer
import com.mobile.app.domain.model.device.Device
import java.math.BigDecimal
import java.time.LocalDateTime

data class Purchase(
    val id: String,
    val purchaseNumber: String,
    val customerId: String,
    val deviceId: String,
    val customer: Customer?,
    val device: Device?,
    val suggestedPrice: BigDecimal,
    val negotiatedPrice: BigDecimal,
    val finalPrice: BigDecimal,
    val notes: String?,
    val status: PurchaseStatus,
    val branchId: String,
    val employeeId: String,
    val createdBy: String,
    val consentId: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val completedAt: LocalDateTime?
)
