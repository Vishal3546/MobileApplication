package com.mobile.app.presentation.device.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.DeviceCreate
import com.mobile.app.domain.model.device.ModelInfo
import com.mobile.app.domain.repository.PhoneSpecsRepository
import com.mobile.app.domain.repository.device.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateDeviceViewModel @Inject constructor(
    private val repository: DeviceRepository,
    private val phoneSpecsRepository: PhoneSpecsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateDeviceUiState>(CreateDeviceUiState.Idle)
    val uiState: StateFlow<CreateDeviceUiState> = _uiState.asStateFlow()

    private val _models = MutableStateFlow<List<ModelInfo>>(emptyList())
    val models: StateFlow<List<ModelInfo>> = _models.asStateFlow()

    private val _fetchedDevice = MutableStateFlow<Device?>(null)
    val fetchedDevice: StateFlow<Device?> = _fetchedDevice.asStateFlow()

    private val _isFetchingImei = MutableStateFlow(value = false)
    val isFetchingImei: StateFlow<Boolean> = _isFetchingImei.asStateFlow()

    private val _isFetchingModels = MutableStateFlow(value = false)
    val isFetchingModels: StateFlow<Boolean> = _isFetchingModels.asStateFlow()

    fun loadModelsForBrand(brand: String) {
        _models.value = emptyList() // clear old models immediately
        _isFetchingModels.value = true
        
        viewModelScope.launch {
            val result = phoneSpecsRepository.getModelsForBrand(brand)
            result.onSuccess { liveModels ->
                if (liveModels.isNotEmpty()) {
                    _models.value = liveModels
                }
            }
            _isFetchingModels.value = false
        }
    }

    fun fetchDeviceByImei(imei: String) {
        if (imei.length < 15) return
        viewModelScope.launch {
            _isFetchingImei.value = true
            val result = repository.getDeviceInfoByImei(imei)
            result.onSuccess { device ->
                _fetchedDevice.value = device
                _isFetchingImei.value = false
            }.onFailure {
                _isFetchingImei.value = false
            }
        }
    }

    fun createDevice(
        brand: String,
        model: String,
        variant: String?,
        color: String,
        storage: String,
        ram: String,
        imei1: String,
        imei2: String?,
        serialNumber: String?,
    ) {
        // Disabled strict IMEI validation for testing purposes
        /*if (!validateImei(imei1)) {
            _uiState.value = CreateDeviceUiState.Error("Invalid IMEI 1")
            return
        }

        if ((imei2 != null) && !validateImei(imei2)) {
            _uiState.value = CreateDeviceUiState.Error("Invalid IMEI 2")
            return
        }*/

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
                    serialNumber = serialNumber?.trim(),
                ),
            )

            result.fold(
                onSuccess = { _uiState.value = CreateDeviceUiState.Success(it.id) },
            ) {
                val msg = if (it.message?.contains("DEVICE_IMEI_ALREADY_EXISTS") == true) {
                    "This IMEI already exists in the system."
                } else {
                    it.message ?: "Failed to create device"
                }
                _uiState.value = CreateDeviceUiState.Error(msg)
            }
        }
    }

    private fun validateImei(imei: String): Boolean {
        val cleanImei = imei.trim()
        if ((cleanImei.length != 15) || !cleanImei.all { it.isDigit() }) return false

        var sum = 0
        var alternate = false
        for (i in (cleanImei.length - 1) downTo 0) {
            var n = cleanImei[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) n = (n % 10) + 1
            }
            sum += n
            alternate = !alternate
        }
        return (sum % 10) == 0
    }
}

sealed class CreateDeviceUiState {
    object Idle : CreateDeviceUiState()
    object Loading : CreateDeviceUiState()
    data class Success(val deviceId: String) : CreateDeviceUiState()
    data class Error(val message: String) : CreateDeviceUiState()
}
