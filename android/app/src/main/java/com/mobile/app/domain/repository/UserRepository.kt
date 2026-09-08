package com.mobile.app.domain.repository

import com.mobile.app.data.remote.dto.user.CreateUserRequestDto
import com.mobile.app.data.remote.dto.user.RoleResponseDto
import com.mobile.app.data.remote.dto.user.UserResponseDto
import com.mobile.app.domain.model.NetworkState

interface UserRepository {
    suspend fun getRoles(): NetworkState<List<RoleResponseDto>>
    suspend fun createUser(request: CreateUserRequestDto): NetworkState<UserResponseDto>
}
