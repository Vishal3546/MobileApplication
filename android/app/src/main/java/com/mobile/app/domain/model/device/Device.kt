package com.mobile.app.domain.model.device

import java.time.LocalDateTime

data class Device(
    val id: String,
    val brand: String,
    val model: String,
    val variant: String?,
    val color: String,
    val storage: String,
    val ram: String,
    val imei1: String,
    val imei2: String?,
    val serialNumber: String?,
    val status: DeviceStatus,
    val branchId: String,
    val createdBy: String,
    val updatedBy: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val verificationState: ImeiVerificationState,
    val mediaCount: Int,
    val latestCondition: DeviceCondition?,
    val latestInspection: DeviceInspection?
)

data class DeviceCreate(
    val brand: String,
    val model: String,
    val variant: String?,
    val color: String,
    val storage: String,
    val ram: String,
    val imei1: String,
    val imei2: String?,
    val serialNumber: String?
)

data class DeviceUpdate(
    val brand: String,
    val model: String,
    val variant: String?,
    val color: String,
    val storage: String,
    val ram: String,
    val imei1: String,
    val imei2: String?,
    val serialNumber: String?
)
