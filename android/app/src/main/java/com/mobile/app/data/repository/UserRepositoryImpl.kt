package com.mobile.app.data.repository

import com.mobile.app.data.remote.api.UserApi
import com.mobile.app.data.remote.dto.user.CreateUserRequestDto
import com.mobile.app.data.remote.dto.user.RoleResponseDto
import com.mobile.app.data.remote.dto.user.UserResponseDto
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.UnknownHostException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {
    override suspend fun getRoles(): NetworkState<List<RoleResponseDto>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getRoles()
            if (response.isSuccessful) {
                NetworkState.Success(response.body() ?: emptyList())
            } else {
                NetworkState.Error("Failed to fetch roles: ${response.code()}")
            }
        } catch (e: UnknownHostException) {
            NetworkState.Offline
        } catch (e: Exception) {
            NetworkState.Error(e.message ?: "An unknown error occurred")
        }
    }

    override suspend fun createUser(request: CreateUserRequestDto): NetworkState<UserResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = api.createUser(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkState.Success(it)
                } ?: NetworkState.Error("Empty response body")
            } else {
                NetworkState.Error("Failed to create user: ${response.code()}")
            }
        } catch (e: UnknownHostException) {
            NetworkState.Offline
        } catch (e: Exception) {
            NetworkState.Error(e.message ?: "An unknown error occurred")
        }
    }
}
