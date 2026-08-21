package com.mobile.app.presentation.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.data.remote.dto.CaptureConsentRequestDto
import com.mobile.app.domain.model.Consent
import com.mobile.app.domain.repository.ConsentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class ConsentListState {
    object Loading : ConsentListState()
    data class Success(val consents: List<Consent>) : ConsentListState()
    data class Error(val message: String) : ConsentListState()
}

sealed class ConsentActionState {
    object Idle : ConsentActionState()
    object Loading : ConsentActionState()
    object Success : ConsentActionState()
    data class Error(val message: String) : ConsentActionState()
}

@HiltViewModel
class ConsentViewModel @Inject constructor(
    private val consentRepository: ConsentRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<ConsentListState>(ConsentListState.Loading)
    val listState: StateFlow<ConsentListState> = _listState.asStateFlow()

    private val _actionState = MutableStateFlow<ConsentActionState>(ConsentActionState.Idle)
    val actionState: StateFlow<ConsentActionState> = _actionState.asStateFlow()

    fun loadConsents(customerId: UUID) {
        _listState.value = ConsentListState.Loading
        viewModelScope.launch {
            consentRepository.getCustomerConsents(customerId)
                .onSuccess { _listState.value = ConsentListState.Success(it) }
                .onFailure { _listState.value = ConsentListState.Error(it.message ?: "Failed to load consents") }
        }
    }

    fun captureConsent(customerId: UUID, request: CaptureConsentRequestDto) {
        _actionState.value = ConsentActionState.Loading
        viewModelScope.launch {
            consentRepository.captureConsent(customerId, request)
                .onSuccess { 
                    _actionState.value = ConsentActionState.Success 
                    loadConsents(customerId)
                }
                .onFailure { _actionState.value = ConsentActionState.Error(it.message ?: "Failed to capture consent") }
        }
    }

    fun resetActionState() {
        _actionState.value = ConsentActionState.Idle
    }
}
