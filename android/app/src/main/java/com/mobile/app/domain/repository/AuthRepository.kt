package com.mobile.app.domain.repository

import com.mobile.app.domain.model.CurrentUser
import com.mobile.app.domain.model.NetworkState

interface AuthRepository {
    suspend fun login(username: String, password: String): NetworkState<CurrentUser>
    suspend fun logout(): NetworkState<Unit>
    fun hasValidSession(): Boolean
    fun clearSession()
}
