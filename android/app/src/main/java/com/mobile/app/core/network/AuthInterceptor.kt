package com.mobile.app.core.network

import com.mobile.app.core.security.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Skip adding token to login or refresh endpoints
        val path = request.url.encodedPath
        if (path.contains("/api/v1/auth/login") || path.contains("/api/v1/auth/refresh")) {
            return chain.proceed(request)
        }

        val token = tokenStorage.getAccessToken()
        if (!token.isNullOrEmpty()) {
            val newRequest = request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}
