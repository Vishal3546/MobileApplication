package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.device.DeviceConditionCreateDto
import com.mobile.app.data.remote.dto.device.DeviceConditionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceConditionApi {
    @POST("api/v1/devices/{id}/conditions")
    suspend fun createCondition(
        @Path("id") id: String,
        @Body conditionCreateDto: DeviceConditionCreateDto
    ): DeviceConditionDto

    @GET("api/v1/devices/{id}/conditions/history")
    suspend fun getConditionHistory(@Path("id") id: String): List<DeviceConditionDto>
}
