package com.mobile.app.data.remote.dto.user

import java.util.UUID

data class RoleResponseDto(
    val id: UUID,
    val name: String,
    val description: String?
)

data class CreateUserRequestDto(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val roleIds: List<UUID>? = null,
    val shopId: UUID? = null
)

data class UserResponseDto(
    val id: UUID,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String
)
