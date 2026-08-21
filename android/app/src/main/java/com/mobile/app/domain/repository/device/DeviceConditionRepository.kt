package com.mobile.app.domain.repository.device

import com.mobile.app.domain.model.device.DeviceCondition
import com.mobile.app.domain.model.device.DeviceConditionCreate

interface DeviceConditionRepository {
    suspend fun createCondition(deviceId: String, condition: DeviceConditionCreate): Result<DeviceCondition>
    suspend fun getConditionHistory(deviceId: String): Result<List<DeviceCondition>>
}
