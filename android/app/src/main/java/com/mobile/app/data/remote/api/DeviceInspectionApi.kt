package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.device.DeviceInspectionCreateDto
import com.mobile.app.data.remote.dto.device.DeviceInspectionDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceInspectionApi {
    @POST("api/v1/devices/{id}/inspections")
    suspend fun createInspection(
        @Path("id") id: String,
        @Body inspectionCreateDto: DeviceInspectionCreateDto
    ): DeviceInspectionDto

    @GET("api/v1/devices/{id}/inspections/history")
    suspend fun getInspectionHistory(@Path("id") id: String): List<DeviceInspectionDto>
}
