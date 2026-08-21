package com.mobile.app.data.repository.device

import com.mobile.app.data.mapper.device.DeviceInspectionMapper
import com.mobile.app.data.remote.api.DeviceInspectionApi
import com.mobile.app.domain.model.device.DeviceInspection
import com.mobile.app.domain.model.device.DeviceInspectionCreate
import com.mobile.app.domain.repository.device.DeviceInspectionRepository

class DeviceInspectionRepositoryImpl(
    private val api: DeviceInspectionApi
) : DeviceInspectionRepository {

    override suspend fun createInspection(
        deviceId: String,
        inspection: DeviceInspectionCreate
    ): Result<DeviceInspection> {
        return try {
            val dto = DeviceInspectionMapper.mapToDto(inspection)
            val response = api.createInspection(deviceId, dto)
            Result.success(DeviceInspectionMapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getInspectionHistory(deviceId: String): Result<List<DeviceInspection>> {
        return try {
            val response = api.getInspectionHistory(deviceId)
            Result.success(response.map { DeviceInspectionMapper.mapToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
