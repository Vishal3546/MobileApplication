package com.mobile.app.presentation.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.purchase.Purchase
import com.mobile.app.domain.model.purchase.PurchaseStatus
import com.mobile.app.domain.repository.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class WizardState(
    val purchaseId: String? = null,
    val currentPurchase: Purchase? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PurchaseWizardViewModel @Inject constructor(
    private val repository: PurchaseRepository
) : ViewModel() {
    private val _wizardState = MutableStateFlow(WizardState())
    val wizardState: StateFlow<WizardState> = _wizardState

    fun createPurchase(
        customerId: String,
        deviceId: String,
        suggestedPrice: BigDecimal,
        negotiatedPrice: BigDecimal,
        finalPrice: BigDecimal,
        notes: String?
    ) {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = repository.createPurchase(customerId, deviceId, suggestedPrice, negotiatedPrice, finalPrice, notes)
            result.onSuccess { purchase ->
                _wizardState.value = _wizardState.value.copy(
                    purchaseId = purchase.id,
                    currentPurchase = purchase,
                    isLoading = false
                )
            }.onFailure { e ->
                _wizardState.value = _wizardState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadPurchase(id: String) {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = repository.getPurchase(id)
            result.onSuccess { purchase ->
                _wizardState.value = _wizardState.value.copy(
                    purchaseId = purchase.id,
                    currentPurchase = purchase,
                    isLoading = false
                )
            }.onFailure { e ->
                _wizardState.value = _wizardState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun completePurchase() {
        val id = _wizardState.value.purchaseId ?: return
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = repository.completePurchase(id)
            result.onSuccess { purchase ->
                _wizardState.value = _wizardState.value.copy(
                    currentPurchase = purchase,
                    isLoading = false
                )
            }.onFailure { e ->
                _wizardState.value = _wizardState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
