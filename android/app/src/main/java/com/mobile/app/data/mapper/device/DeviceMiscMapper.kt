package com.mobile.app.data.mapper.device

import com.mobile.app.data.remote.dto.device.DeviceMediaDto
import com.mobile.app.data.remote.dto.device.ImeiVerificationResultDto
import com.mobile.app.domain.model.device.DeviceMedia
import com.mobile.app.domain.model.device.DeviceMediaType
import com.mobile.app.domain.model.device.ImeiVerificationResult
import com.mobile.app.domain.model.device.ImeiVerificationState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DeviceMiscMapper {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    fun mapToDomain(dto: ImeiVerificationResultDto): ImeiVerificationResult {
        return ImeiVerificationResult(
            deviceId = dto.deviceId,
            state = ImeiVerificationState.valueOf(dto.state),
            message = dto.message,
            verifiedAt = LocalDateTime.parse(dto.verifiedAt, formatter)
        )
    }

    fun mapToDomain(dto: DeviceMediaDto): DeviceMedia {
        return DeviceMedia(
            id = dto.id,
            deviceId = dto.deviceId,
            mediaId = dto.mediaId,
            type = DeviceMediaType.valueOf(dto.type),
            url = dto.url,
            uploadedBy = dto.uploadedBy,
            uploadedAt = LocalDateTime.parse(dto.uploadedAt, formatter)
        )
    }
}
