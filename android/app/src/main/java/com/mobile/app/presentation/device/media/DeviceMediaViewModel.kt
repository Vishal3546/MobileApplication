package com.mobile.app.presentation.device.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.device.DeviceMedia
import com.mobile.app.domain.model.device.DeviceMediaType
import com.mobile.app.domain.repository.device.DeviceMediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DeviceMediaViewModel @Inject constructor(
    private val repository: DeviceMediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DeviceMediaUiState>(DeviceMediaUiState.Idle)
    val uiState: StateFlow<DeviceMediaUiState> = _uiState.asStateFlow()

    private val _mediaList = MutableStateFlow<List<DeviceMedia>>(emptyList())
    val mediaList: StateFlow<List<DeviceMedia>> = _mediaList.asStateFlow()

    fun uploadMedia(deviceId: String, type: DeviceMediaType, file: File) {
        _uiState.value = DeviceMediaUiState.Uploading(type)
        viewModelScope.launch {
            val uploadResult = repository.uploadMedia(file)
            uploadResult.fold(
                onSuccess = { mediaId ->
                    linkMedia(deviceId, mediaId, type)
                },
                onFailure = { _uiState.value = DeviceMediaUiState.Error(it.message ?: "Upload failed") }
            )
        }
    }

    fun linkMedia(deviceId: String, mediaId: String, type: DeviceMediaType) {
        _uiState.value = DeviceMediaUiState.Linking(mediaId, type)
        viewModelScope.launch {
            val linkResult = repository.linkMedia(deviceId, mediaId, type)
            linkResult.fold(
                onSuccess = { 
                    _uiState.value = DeviceMediaUiState.UploadSuccess(it)
                    loadMedia(deviceId)
                },
                onFailure = { 
                    _uiState.value = DeviceMediaUiState.LinkFailed(mediaId, type, it.message ?: "Linking failed")
                }
            )
        }
    }

    fun loadMedia(deviceId: String) {
        viewModelScope.launch {
            val result = repository.getDeviceMedia(deviceId)
            result.fold(
                onSuccess = { _mediaList.value = it },
                onFailure = { _uiState.value = DeviceMediaUiState.Error(it.message ?: "Failed to load media") }
            )
        }
    }
}

sealed class DeviceMediaUiState {
    object Idle : DeviceMediaUiState()
    data class Uploading(val type: DeviceMediaType) : DeviceMediaUiState()
    data class Linking(val mediaId: String, val type: DeviceMediaType) : DeviceMediaUiState()
    data class UploadSuccess(val media: DeviceMedia) : DeviceMediaUiState()
    data class LinkFailed(val mediaId: String, val type: DeviceMediaType, val message: String) : DeviceMediaUiState()
    data class Error(val message: String) : DeviceMediaUiState()
}
