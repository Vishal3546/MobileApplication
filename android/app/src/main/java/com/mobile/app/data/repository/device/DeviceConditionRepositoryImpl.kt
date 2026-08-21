package com.mobile.app.data.repository.device

import com.mobile.app.data.mapper.device.DeviceConditionMapper
import com.mobile.app.data.remote.api.DeviceConditionApi
import com.mobile.app.domain.model.device.DeviceCondition
import com.mobile.app.domain.model.device.DeviceConditionCreate
import com.mobile.app.domain.repository.device.DeviceConditionRepository

class DeviceConditionRepositoryImpl(
    private val api: DeviceConditionApi
) : DeviceConditionRepository {

    override suspend fun createCondition(
        deviceId: String,
        condition: DeviceConditionCreate
    ): Result<DeviceCondition> {
        return try {
            val dto = DeviceConditionMapper.mapToDto(condition)
            val response = api.createCondition(deviceId, dto)
            Result.success(DeviceConditionMapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getConditionHistory(deviceId: String): Result<List<DeviceCondition>> {
        return try {
            val response = api.getConditionHistory(deviceId)
            Result.success(response.map { DeviceConditionMapper.mapToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
