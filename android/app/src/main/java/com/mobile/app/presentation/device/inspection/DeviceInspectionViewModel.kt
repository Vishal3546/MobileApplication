package com.mobile.app.presentation.device.inspection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.DeviceInspection
import com.mobile.app.domain.model.device.DeviceInspectionCreate
import com.mobile.app.domain.model.device.InspectionStatus
import com.mobile.app.domain.repository.device.DeviceInspectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceInspectionViewModel @Inject constructor(
    private val repository: DeviceInspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceInspectionUiState>(DeviceInspectionUiState.Idle)
    val uiState: StateFlow<DeviceInspectionUiState> = _uiState.asStateFlow()

    private val _history = MutableStateFlow<List<DeviceInspection>>(emptyList())
    val history: StateFlow<List<DeviceInspection>> = _history.asStateFlow()

    fun createInspection(deviceId: String, inspection: DeviceInspectionCreate) {
        val mandatoryTests = listOf(
            inspection.display,
            inspection.touch,
            inspection.battery,
            inspection.charging,
            inspection.network
        )

        if (mandatoryTests.any { it == InspectionStatus.NOT_TESTED }) {
            _uiState.value = DeviceInspectionUiState.Error("Mandatory tests must be completed")
            return
        }

        _uiState.value = DeviceInspectionUiState.Loading
        viewModelScope.launch {
            val result = repository.createInspection(deviceId, inspection)
            result.fold(
                onSuccess = { _uiState.value = DeviceInspectionUiState.Success(it) },
                onFailure = { _uiState.value = DeviceInspectionUiState.Error(it.message ?: "Failed to create inspection") }
            )
        }
    }

    fun loadHistory(deviceId: String) {
        viewModelScope.launch {
            val result = repository.getInspectionHistory(deviceId)
            result.fold(
                onSuccess = { _history.value = it },
                onFailure = { _uiState.value = DeviceInspectionUiState.Error(it.message ?: "Failed to load history") }
            )
        }
    }
}

sealed class DeviceInspectionUiState {
    object Idle : DeviceInspectionUiState()
    object Loading : DeviceInspectionUiState()
    data class Success(val inspection: DeviceInspection) : DeviceInspectionUiState()
    data class Error(val message: String) : DeviceInspectionUiState()
}
