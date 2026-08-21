package com.mobile.app.data.mapper

import com.mobile.app.data.remote.dto.purchase.*
import com.mobile.app.domain.model.purchase.*
import com.mobile.app.domain.model.Customer
import com.mobile.app.data.remote.dto.CustomerDto
import com.mobile.app.data.mapper.device.DeviceMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun PurchaseStatusDto.toDomain(): PurchaseStatus {
    return PurchaseStatus.valueOf(this.name)
}

fun PurchaseStatus.toDto(): PurchaseStatusDto {
    return PurchaseStatusDto.valueOf(this.name)
}

fun PaymentModeDto.toDomain(): PaymentMode {
    return PaymentMode.valueOf(this.name)
}

fun PaymentStatusDto.toDomain(): PaymentStatus {
    return PaymentStatus.valueOf(this.name)
}

fun PurchaseDto.toDomain(): Purchase {
    return Purchase(
        id = id,
        purchaseNumber = purchaseNumber,
        customerId = customerId,
        deviceId = deviceId,
        customer = customer?.toDomain(),
        device = device?.let { DeviceMapper.mapToDomain(it) },
        suggestedPrice = suggestedPrice,
        negotiatedPrice = negotiatedPrice,
        finalPrice = finalPrice,
        notes = notes,
        status = status.toDomain(),
        branchId = branchId,
        employeeId = employeeId,
        createdBy = createdBy,
        consentId = consentId,
        createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME),
        completedAt = completedAt?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
    )
}

fun PurchasePaymentDto.toDomain(): PurchasePayment {
    return PurchasePayment(
        id = id,
        amount = amount,
        referenceNumber = referenceNumber,
        paymentMode = paymentMode.toDomain(),
        status = status.toDomain(),
        idempotencyKey = idempotencyKey,
        createdAt = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME)
    )
}
