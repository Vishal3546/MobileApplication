package com.mobile.app.data.mapper.device

import com.mobile.app.data.remote.dto.device.DeviceDto
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.DeviceStatus
import com.mobile.app.domain.model.device.ImeiVerificationState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DeviceMapper {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    fun mapToDomain(dto: DeviceDto): Device {
        return Device(
            id = dto.id,
            brand = dto.brand,
            model = dto.model,
            variant = dto.variant,
            color = dto.color,
            storage = dto.storage,
            ram = dto.ram,
            imei1 = dto.imei1,
            imei2 = dto.imei2,
            serialNumber = dto.serialNumber,
            status = DeviceStatus.valueOf(dto.status),
            branchId = dto.branchId,
            createdBy = dto.createdBy,
            updatedBy = dto.updatedBy,
            createdAt = LocalDateTime.parse(dto.createdAt, formatter),
            updatedAt = LocalDateTime.parse(dto.updatedAt, formatter),
            verificationState = ImeiVerificationState.valueOf(dto.verificationState),
            mediaCount = dto.mediaCount,
            latestCondition = dto.latestCondition?.let { DeviceConditionMapper.mapToDomain(it) },
            latestInspection = dto.latestInspection?.let { DeviceInspectionMapper.mapToDomain(it) }
        )
    }
}
