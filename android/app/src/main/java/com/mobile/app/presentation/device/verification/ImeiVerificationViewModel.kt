package com.mobile.app.presentation.device.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.ImeiVerificationResult
import com.mobile.app.domain.repository.device.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImeiVerificationViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImeiVerificationUiState>(ImeiVerificationUiState.Idle)
    val uiState: StateFlow<ImeiVerificationUiState> = _uiState.asStateFlow()

    fun verifyImei(deviceId: String) {
        _uiState.value = ImeiVerificationUiState.Loading
        viewModelScope.launch {
            val result = repository.verifyImei(deviceId)
            result.fold(
                onSuccess = { _uiState.value = ImeiVerificationUiState.Success(it) },
                onFailure = { _uiState.value = ImeiVerificationUiState.Error(it.message ?: "Verification failed") }
            )
        }
    }
}

sealed class ImeiVerificationUiState {
    object Idle : ImeiVerificationUiState()
    object Loading : ImeiVerificationUiState()
    data class Success(val result: ImeiVerificationResult) : ImeiVerificationUiState()
    data class Error(val message: String) : ImeiVerificationUiState()
}
