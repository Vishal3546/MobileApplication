package com.mobile.app.domain.model

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    object Authenticated : AuthState()
    object SessionExpired : AuthState()
}
