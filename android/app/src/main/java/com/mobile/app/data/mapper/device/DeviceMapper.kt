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
            brand = dto.brand ?: "Unknown",
            model = dto.model ?: "Unknown",
            variant = dto.variant,
            color = dto.color ?: "N/A",
            storage = dto.storage ?: "N/A",
            ram = dto.ram ?: "N/A",
            imei1 = dto.imei1 ?: "",
            imei2 = dto.imei2,
            serialNumber = dto.serialNumber,
            status = DeviceStatus.valueOf(dto.status ?: "ACTIVE"),
            branchId = dto.branchId ?: "",
            createdBy = dto.createdBy ?: "",
            updatedBy = dto.updatedBy,
            createdAt = dto.createdAt?.let { LocalDateTime.parse(it, formatter) } ?: LocalDateTime.now(),
            updatedAt = dto.updatedAt?.let { LocalDateTime.parse(it, formatter) } ?: LocalDateTime.now(),
            verificationState = ImeiVerificationState.valueOf(dto.verificationState ?: "UNVERIFIED"),
            mediaCount = dto.mediaCount,
            latestCondition = dto.latestCondition?.let { DeviceConditionMapper.mapToDomain(it) },
            latestInspection = dto.latestInspection?.let { DeviceInspectionMapper.mapToDomain(it) }
        )
    }
}
