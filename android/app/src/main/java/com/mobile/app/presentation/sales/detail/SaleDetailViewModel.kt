package com.mobile.app.presentation.sales.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.sales.SaleTransaction
import com.mobile.app.domain.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SaleDetailViewModel @Inject constructor(
    private val repository: SaleRepository
) : ViewModel() {

    private val _sale = MutableStateFlow<SaleTransaction?>(null)
    val sale: StateFlow<SaleTransaction?> = _sale

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadSale(id: UUID) {
        viewModelScope.launch {
            when (val result = repository.getSaleById(id)) {
                is NetworkState.Success -> _sale.value = result.data
                is NetworkState.Error -> _error.value = result.message
                is NetworkState.Loading -> {}
                is NetworkState.Offline -> _error.value = "Device is offline"
            }
        }
    }

    fun cancelSale(id: UUID, reason: String) {
        viewModelScope.launch {
            when (val result = repository.cancelSale(id, reason)) {
                is NetworkState.Success -> _sale.value = result.data
                is NetworkState.Error -> _error.value = "Failed to cancel: ${result.message}"
                is NetworkState.Loading -> {}
                is NetworkState.Offline -> _error.value = "Device is offline"
            }
        }
    }
}
