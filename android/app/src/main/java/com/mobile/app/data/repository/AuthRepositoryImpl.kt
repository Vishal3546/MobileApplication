package com.mobile.app.data.repository

import com.mobile.app.core.network.ApiErrorMapper
import com.mobile.app.core.security.TokenStorage
import com.mobile.app.data.remote.AuthApi
import com.mobile.app.data.remote.dto.LoginRequest
import com.mobile.app.domain.model.CurrentUser
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    override suspend fun login(username: String, password: String): NetworkState<CurrentUser> {
        return try {
            val response = authApi.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                tokenStorage.saveAccessToken(data.accessToken)
                tokenStorage.saveRefreshToken(data.refreshToken)
                
                val currentUser = CurrentUser(
                    id = data.id ?: "",
                    username = data.username ?: username,
                    roles = data.roles ?: emptyList(),
                    permissions = data.permissions ?: emptyList()
                )
                NetworkState.Success(currentUser)
            } else {
                val errorMsg = try {
                    val errorBody = response.errorBody()?.string()
                    if (!errorBody.isNullOrEmpty()) {
                        val json = org.json.JSONObject(errorBody)
                        json.optString("message", "Invalid username or password")
                    } else response.body()?.message ?: "Login failed"
                } catch (e: Exception) {
                    if (response.code() == 401) "Invalid username or password" else response.message()
                }

                val type = when (response.code()) {
                    401 -> com.mobile.app.domain.model.ApiErrorType.SessionExpired
                    400 -> com.mobile.app.domain.model.ApiErrorType.ValidationError
                    403 -> com.mobile.app.domain.model.ApiErrorType.PermissionDenied
                    else -> com.mobile.app.domain.model.ApiErrorType.Unknown
                }
                NetworkState.Error(type, errorMsg)
            }
        } catch (e: Exception) {
            val (type, msg) = ApiErrorMapper.map(e)
            if (type == com.mobile.app.domain.model.ApiErrorType.Unknown && e is java.io.IOException) {
                NetworkState.Offline
            } else {
                NetworkState.Error(type, msg)
            }
        }
    }

    override suspend fun logout(): NetworkState<Unit> {
        return try {
            authApi.logout()
            // Always clear session locally regardless of server success, to prevent state trap
            clearSession()
            NetworkState.Success(Unit)
        } catch (e: Exception) {
            clearSession() // Always clear local session
            val (type, msg) = ApiErrorMapper.map(e)
            if (type == com.mobile.app.domain.model.ApiErrorType.Unknown && e is java.io.IOException) {
                NetworkState.Offline
            } else {
                NetworkState.Error(type, msg)
            }
        }
    }

    override fun hasValidSession(): Boolean {
        // Basic check if token exists. A real JWT parse could check expiration time here.
        return !tokenStorage.getAccessToken().isNullOrEmpty()
    }

    override fun clearSession() {
        tokenStorage.clearTokens()
    }
}
