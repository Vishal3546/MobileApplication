package com.mobile.app.presentation.device.condition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.ConditionStatus
import com.mobile.app.domain.model.device.DeviceCondition
import com.mobile.app.domain.model.device.DeviceConditionCreate
import com.mobile.app.domain.repository.device.DeviceConditionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceConditionViewModel @Inject constructor(
    private val repository: DeviceConditionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceConditionUiState>(DeviceConditionUiState.Idle)
    val uiState: StateFlow<DeviceConditionUiState> = _uiState.asStateFlow()

    private val _history = MutableStateFlow<List<DeviceCondition>>(emptyList())
    val history: StateFlow<List<DeviceCondition>> = _history.asStateFlow()

    fun createCondition(
        deviceId: String,
        batteryHealth: Int,
        displayCondition: ConditionStatus,
        bodyCondition: ConditionStatus,
        cameraCondition: ConditionStatus,
        speakerCondition: ConditionStatus,
        microphoneCondition: ConditionStatus,
        chargingCondition: ConditionStatus,
        biometricStatus: ConditionStatus,
        networkLock: Boolean,
        originalBill: Boolean,
        box: Boolean,
        charger: Boolean,
        accessories: Boolean,
        notes: String?
    ) {
        if (batteryHealth !in 0..100) {
            _uiState.value = DeviceConditionUiState.Error("Battery health must be between 0 and 100")
            return
        }

        _uiState.value = DeviceConditionUiState.Loading
        viewModelScope.launch {
            val result = repository.createCondition(
                deviceId,
                DeviceConditionCreate(
                    batteryHealth = batteryHealth,
                    displayCondition = displayCondition,
                    bodyCondition = bodyCondition,
                    cameraCondition = cameraCondition,
                    speakerCondition = speakerCondition,
                    microphoneCondition = microphoneCondition,
                    chargingCondition = chargingCondition,
                    biometricStatus = biometricStatus,
                    networkLock = networkLock,
                    originalBill = originalBill,
                    box = box,
                    charger = charger,
                    accessories = accessories,
                    notes = notes
                )
            )

            result.fold(
                onSuccess = { _uiState.value = DeviceConditionUiState.Success(it) },
                onFailure = { _uiState.value = DeviceConditionUiState.Error(it.message ?: "Failed to create condition") }
            )
        }
    }

    fun loadHistory(deviceId: String) {
        viewModelScope.launch {
            val result = repository.getConditionHistory(deviceId)
            result.fold(
                onSuccess = { _history.value = it },
                onFailure = { _uiState.value = DeviceConditionUiState.Error(it.message ?: "Failed to load history") }
            )
        }
    }
}

sealed class DeviceConditionUiState {
    object Idle : DeviceConditionUiState()
    object Loading : DeviceConditionUiState()
    data class Success(val condition: DeviceCondition) : DeviceConditionUiState()
    data class Error(val message: String) : DeviceConditionUiState()
}
