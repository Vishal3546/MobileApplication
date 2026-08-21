package com.mobile.app.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface HealthApi {
    @GET("actuator/health")
    suspend fun checkHealth(): Response<Unit>
}
