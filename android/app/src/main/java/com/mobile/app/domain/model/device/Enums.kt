package com.mobile.app.domain.model.device

enum class DeviceStatus {
    ACTIVE,
    BLOCKED
}

enum class ConditionStatus {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    DAMAGED,
    UNKNOWN
}

enum class InspectionStatus {
    PASS,
    FAIL,
    NOT_TESTED,
    NOT_APPLICABLE
}

enum class DeviceMediaType {
    FRONT,
    BACK,
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    SCREEN_ON,
    SCREEN_OFF,
    IMEI_SCREEN,
    DAMAGE,
    OTHER
}

enum class ImeiVerificationState {
    VERIFIED,
    INVALID,
    DUPLICATE,
    BLOCKED,
    ERROR,
    UNVERIFIED
}

enum class LifecycleEventType {
    DEVICE_CREATED,
    DEVICE_UPDATED,
    IMEI_VERIFIED,
    CONDITION_RECORDED,
    INSPECTION_CREATED,
    DEVICE_MEDIA_ADDED,
    DEVICE_BLOCKED,
    DEVICE_UNBLOCKED
}
