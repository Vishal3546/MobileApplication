package com.mobile.app.data.mapper

import com.mobile.app.data.remote.dto.sales.SalePaymentResponse
import com.mobile.app.data.remote.dto.sales.SaleTransactionResponse
import com.mobile.app.domain.model.sales.SalePayment
import com.mobile.app.domain.model.sales.SaleTransaction

fun SaleTransactionResponse.toDomain(): SaleTransaction {
    return SaleTransaction(
        id = id,
        saleNumber = saleNumber,
        customerId = customerId,
        inventoryItem = inventoryItem?.toDomain(),
        branchId = branchId,
        sellingPrice = sellingPrice,
        discount = discount,
        tax = tax,
        finalAmount = finalAmount,
        paymentStatus = paymentStatus,
        saleStatus = saleStatus,
        warrantyStart = warrantyStart,
        warrantyEnd = warrantyEnd,
        createdBy = createdBy,
        createdAt = createdAt,
        completedAt = completedAt
    )
}

fun SalePaymentResponse.toDomain(): SalePayment {
    return SalePayment(
        id = id,
        saleTransactionId = saleTransactionId,
        paymentMode = paymentMode,
        amount = amount,
        referenceNumber = referenceNumber,
        status = status,
        createdAt = createdAt
    )
}
