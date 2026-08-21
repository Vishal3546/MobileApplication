package com.mobile.app.presentation.purchase.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.purchase.Purchase
import com.mobile.app.domain.repository.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseDetailViewModel @Inject constructor(
    private val repository: PurchaseRepository
) : ViewModel() {
    private val _purchase = MutableStateFlow<Purchase?>(null)
    val purchase: StateFlow<Purchase?> = _purchase

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPurchase(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getPurchase(id)
            result.onSuccess { p ->
                _purchase.value = p
            }.onFailure { e ->
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }
    
    fun cancelPurchase(id: String, reason: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.cancelPurchase(id, reason)
            result.onSuccess { p ->
                _purchase.value = p
            }.onFailure { e ->
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }
}
