package com.mobile.app.presentation.device.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.DeviceLifecycleEvent
import com.mobile.app.domain.repository.device.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceLifecycleViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<DeviceLifecycleEvent>>(emptyList())
    val events: StateFlow<List<DeviceLifecycleEvent>> = _events.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadLifecycle(deviceId: String) {
        viewModelScope.launch {
            val result = repository.getDeviceLifecycle(deviceId)
            result.fold(
                onSuccess = { 
                    _events.value = it
                    _error.value = null
                },
                onFailure = { 
                    _error.value = it.message ?: "Failed to load lifecycle" 
                }
            )
        }
    }
}
