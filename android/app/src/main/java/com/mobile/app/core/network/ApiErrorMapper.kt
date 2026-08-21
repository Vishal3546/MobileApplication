package com.mobile.app.core.network

import com.mobile.app.domain.model.ApiErrorType
import retrofit2.HttpException
import java.io.IOException

object ApiErrorMapper {
    fun map(throwable: Throwable): Pair<ApiErrorType, String?> {
        return when (throwable) {
            is IOException -> Pair(ApiErrorType.Unknown, "Network error occurred")
            is HttpException -> {
                val type = when (throwable.code()) {
                    400 -> ApiErrorType.ValidationError
                    401 -> ApiErrorType.SessionExpired
                    403 -> ApiErrorType.PermissionDenied
                    404 -> ApiErrorType.ResourceNotFound
                    409 -> ApiErrorType.Conflict
                    429 -> ApiErrorType.RateLimited
                    in 500..599 -> ApiErrorType.ServerError
                    else -> ApiErrorType.Unknown
                }
                Pair(type, throwable.message())
            }
            else -> Pair(ApiErrorType.Unknown, throwable.localizedMessage)
        }
    }
}
