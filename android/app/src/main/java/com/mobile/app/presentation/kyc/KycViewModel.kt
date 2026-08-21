package com.mobile.app.presentation.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.data.remote.dto.UploadKycRequestDto
import com.mobile.app.domain.model.KycDocument
import com.mobile.app.domain.repository.KycRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class KycListState {
    object Loading : KycListState()
    data class Success(val documents: List<KycDocument>) : KycListState()
    data class Error(val message: String) : KycListState()
}

sealed class KycActionState {
    object Idle : KycActionState()
    object Loading : KycActionState()
    object Success : KycActionState()
    data class Error(val message: String) : KycActionState()
}

@HiltViewModel
class KycViewModel @Inject constructor(
    private val kycRepository: KycRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<KycListState>(KycListState.Loading)
    val listState: StateFlow<KycListState> = _listState.asStateFlow()

    private val _actionState = MutableStateFlow<KycActionState>(KycActionState.Idle)
    val actionState: StateFlow<KycActionState> = _actionState.asStateFlow()

    fun loadDocuments(customerId: UUID) {
        _listState.value = KycListState.Loading
        viewModelScope.launch {
            kycRepository.getCustomerDocuments(customerId)
                .onSuccess { _listState.value = KycListState.Success(it) }
                .onFailure { _listState.value = KycListState.Error(it.message ?: "Failed to load documents") }
        }
    }

    fun uploadKyc(customerId: UUID, request: UploadKycRequestDto) {
        _actionState.value = KycActionState.Loading
        viewModelScope.launch {
            kycRepository.uploadKyc(customerId, request)
                .onSuccess { 
                    _actionState.value = KycActionState.Success 
                    loadDocuments(customerId)
                }
                .onFailure { _actionState.value = KycActionState.Error(it.message ?: "Failed to upload KYC") }
        }
    }

    fun approveDocument(customerId: UUID, documentId: UUID, notes: String?) {
        _actionState.value = KycActionState.Loading
        viewModelScope.launch {
            kycRepository.approveDocument(customerId, documentId, notes)
                .onSuccess { 
                    _actionState.value = KycActionState.Success 
                    loadDocuments(customerId)
                }
                .onFailure { _actionState.value = KycActionState.Error(it.message ?: "Failed to approve document") }
        }
    }

    fun rejectDocument(customerId: UUID, documentId: UUID, notes: String?) {
        _actionState.value = KycActionState.Loading
        viewModelScope.launch {
            kycRepository.rejectDocument(customerId, documentId, notes)
                .onSuccess { 
                    _actionState.value = KycActionState.Success 
                    loadDocuments(customerId)
                }
                .onFailure { _actionState.value = KycActionState.Error(it.message ?: "Failed to reject document") }
        }
    }

    fun resetActionState() {
        _actionState.value = KycActionState.Idle
    }
}
