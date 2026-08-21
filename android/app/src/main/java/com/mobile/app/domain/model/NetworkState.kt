package com.mobile.app.domain.model

sealed class NetworkState<out T> {
    object Loading : NetworkState<Nothing>()
    data class Success<T>(val data: T) : NetworkState<T>()
    data class Error(val type: ApiErrorType, val message: String?) : NetworkState<Nothing>()
    object Offline : NetworkState<Nothing>()
}

fun <T, R> NetworkState<T>.map(transform: (T) -> R): NetworkState<R> {
    return when (this) {
        is NetworkState.Success -> NetworkState.Success(transform(data))
        is NetworkState.Error -> NetworkState.Error(type, message)
        is NetworkState.Loading -> NetworkState.Loading
        is NetworkState.Offline -> NetworkState.Offline
    }
}

enum class ApiErrorType {
    ValidationError,
    SessionExpired,
    PermissionDenied,
    ResourceNotFound,
    Conflict,
    RateLimited,
    ServerError,
    Unknown
}

suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): NetworkState<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkState.Success(body)
            } else {
                NetworkState.Error(ApiErrorType.Unknown, "Response body is null")
            }
        } else {
            NetworkState.Error(ApiErrorType.ServerError, "Error: ${response.code()}")
        }
    } catch (e: Exception) {
        NetworkState.Error(ApiErrorType.Unknown, e.message)
    }
}
