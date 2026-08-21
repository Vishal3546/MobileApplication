package com.mobile.app.core.network

import com.mobile.app.core.security.TokenStorage
import com.mobile.app.data.remote.AuthApi
import com.mobile.app.data.remote.dto.RefreshTokenRequest
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(
    private val tokenStorage: TokenStorage,
    private val authApi: Lazy<AuthApi>
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val currentToken = tokenStorage.getAccessToken()

        return runBlocking {
            mutex.withLock {
                val updatedToken = tokenStorage.getAccessToken()
                
                // If token was already refreshed by another thread while we were waiting for the lock
                if (currentToken != updatedToken && updatedToken != null) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $updatedToken")
                        .build()
                }

                val refreshToken = tokenStorage.getRefreshToken()
                if (refreshToken.isNullOrEmpty()) {
                    tokenStorage.clearTokens()
                    return@runBlocking null // No refresh token available, fail
                }

                try {
                    val refreshResponse = authApi.get().refresh(RefreshTokenRequest(refreshToken))
                    if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                        val body = refreshResponse.body()!!
                        tokenStorage.saveAccessToken(body.accessToken)
                        tokenStorage.saveRefreshToken(body.refreshToken)

                        response.request.newBuilder()
                            .header("Authorization", "Bearer ${body.accessToken}")
                            .build()
                    } else {
                        // Refresh failed (e.g. refresh token expired)
                        tokenStorage.clearTokens()
                        null
                    }
                } catch (e: Exception) {
                    // Network error during refresh, do not clear tokens yet, let the request fail
                    // Or clear tokens? If it's a true network error, maybe wait. The requirement says:
                    // "If refresh fails: clear session". We'll clear it just to be safe.
                    tokenStorage.clearTokens()
                    null
                }
            }
        }
    }
}
