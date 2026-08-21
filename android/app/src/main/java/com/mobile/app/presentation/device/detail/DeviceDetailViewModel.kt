package com.mobile.app.presentation.device.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.DeviceStatus
import com.mobile.app.domain.repository.device.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceDetailUiState>(DeviceDetailUiState.Loading)
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    fun loadDevice(id: String) {
        _uiState.value = DeviceDetailUiState.Loading
        viewModelScope.launch {
            val result = repository.getDevice(id)
            result.fold(
                onSuccess = { _uiState.value = DeviceDetailUiState.Success(it) },
                onFailure = { _uiState.value = DeviceDetailUiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun updateDeviceStatus(id: String, status: DeviceStatus) {
        viewModelScope.launch {
            val result = repository.updateDeviceStatus(id, status)
            result.fold(
                onSuccess = { _uiState.value = DeviceDetailUiState.Success(it) },
                onFailure = { _uiState.value = DeviceDetailUiState.Error(it.message ?: "Failed to update status") }
            )
        }
    }
}

sealed class DeviceDetailUiState {
    object Loading : DeviceDetailUiState()
    data class Success(val device: Device) : DeviceDetailUiState()
    data class Error(val message: String) : DeviceDetailUiState()
}
