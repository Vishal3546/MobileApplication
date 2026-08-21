package com.mobile.app.presentation.device.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.DeviceCreate
import com.mobile.app.domain.repository.device.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDeviceViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateDeviceUiState>(CreateDeviceUiState.Idle)
    val uiState: StateFlow<CreateDeviceUiState> = _uiState.asStateFlow()

    fun createDevice(
        brand: String,
        model: String,
        variant: String?,
        color: String,
        storage: String,
        ram: String,
        imei1: String,
        imei2: String?,
        serialNumber: String?
    ) {
        if (!validateImei(imei1)) {
            _uiState.value = CreateDeviceUiState.Error("Invalid IMEI 1")
            return
        }

        if (imei2 != null && !validateImei(imei2)) {
            _uiState.value = CreateDeviceUiState.Error("Invalid IMEI 2")
            return
        }

        _uiState.value = CreateDeviceUiState.Loading
        viewModelScope.launch {
            val result = repository.createDevice(
                DeviceCreate(
                    brand = brand,
                    model = model,
                    variant = variant,
                    color = color,
                    storage = storage,
                    ram = ram,
                    imei1 = imei1.trim(),
                    imei2 = imei2?.trim(),
                    serialNumber = serialNumber?.trim()
                )
            )

            result.fold(
                onSuccess = { _uiState.value = CreateDeviceUiState.Success(it.id) },
                onFailure = { 
                    val msg = if (it.message?.contains("DEVICE_IMEI_ALREADY_EXISTS") == true) {
                        "This IMEI already exists in the system."
                    } else {
                        it.message ?: "Failed to create device"
                    }
                    _uiState.value = CreateDeviceUiState.Error(msg) 
                }
            )
        }
    }

    private fun validateImei(imei: String): Boolean {
        val cleanImei = imei.trim()
        if (cleanImei.length != 15 || !cleanImei.all { it.isDigit() }) return false

        var sum = 0
        var alternate = false
        for (i in cleanImei.length - 1 downTo 0) {
            var n = cleanImei[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) n = (n % 10) + 1
            }
            sum += n
            alternate = !alternate
        }
        return sum % 10 == 0
    }
}

sealed class CreateDeviceUiState {
    object Idle : CreateDeviceUiState()
    object Loading : CreateDeviceUiState()
    data class Success(val deviceId: String) : CreateDeviceUiState()
    data class Error(val message: String) : CreateDeviceUiState()
}
