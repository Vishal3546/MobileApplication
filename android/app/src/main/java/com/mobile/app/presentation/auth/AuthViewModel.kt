package com.mobile.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.AuthState
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        if (authRepository.hasValidSession()) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(username: String, password: String, onLoadingChange: (Boolean) -> Unit) {
        viewModelScope.launch {
            _loginError.value = null
            onLoadingChange(true)
            
            when (val result = authRepository.login(username, password)) {
                is NetworkState.Success -> {
                    _authState.value = AuthState.Authenticated
                }
                is NetworkState.Error -> {
                    _loginError.value = result.message ?: "Login failed"
                    _authState.value = AuthState.Unauthenticated
                }
                is NetworkState.Offline -> {
                    _loginError.value = "No internet connection"
                    _authState.value = AuthState.Unauthenticated
                }
                NetworkState.Loading -> { } // Handled by callback
            }
            onLoadingChange(false)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout() // Always clears locally
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun setSessionExpired() {
        _authState.value = AuthState.SessionExpired
        authRepository.clearSession()
    }
}
