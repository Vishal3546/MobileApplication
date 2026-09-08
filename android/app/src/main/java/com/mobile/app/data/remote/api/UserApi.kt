package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.user.CreateUserRequestDto
import com.mobile.app.data.remote.dto.user.RoleResponseDto
import com.mobile.app.data.remote.dto.user.UserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {
    @GET("/api/v1/roles")
    suspend fun getRoles(): Response<List<RoleResponseDto>>

    @POST("/api/v1/users")
    suspend fun createUser(@Body request: CreateUserRequestDto): Response<UserResponseDto>
}
