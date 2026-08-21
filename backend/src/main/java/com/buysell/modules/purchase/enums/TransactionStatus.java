package com.buysell.modules.purchase.enums;

public enum TransactionStatus {
    INITIATED,
    PENDING_KYC,
    PENDING_DEVICE_VERIFICATION,
    PENDING_INSPECTION,
    PENDING_CONSENT,
    PENDING_PAYMENT,
    COMPLETED,
    CANCELLED
}
