package com.mobile.app.domain.model.purchase

enum class PurchaseStatus {
    INITIATED,
    PENDING_KYC,
    PENDING_DEVICE_VERIFICATION,
    PENDING_INSPECTION,
    PENDING_CONSENT,
    PENDING_PAYMENT,
    COMPLETED,
    CANCELLED
}
