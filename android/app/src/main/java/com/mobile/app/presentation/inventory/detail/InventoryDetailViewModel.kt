package com.mobile.app.presentation.inventory.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InventoryDetailViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _inventory = MutableStateFlow<Inventory?>(null)
    val inventory: StateFlow<Inventory?> = _inventory

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadInventory(id: UUID) {
        viewModelScope.launch {
            when (val result = repository.getInventoryById(id)) {
                is NetworkState.Success -> _inventory.value = result.data
                is NetworkState.Error -> _error.value = result.message
                is NetworkState.Loading -> {}
                is NetworkState.Offline -> _error.value = "Device is offline"
            }
        }
    }

    fun reserveInventory(id: UUID) {
        viewModelScope.launch {
            when (val result = repository.reserveInventory(id, null, "Reserved from Android App")) {
                is NetworkState.Success -> _inventory.value = result.data
                is NetworkState.Error -> _error.value = "Failed to reserve: ${result.message}"
                is NetworkState.Loading -> {}
                is NetworkState.Offline -> _error.value = "Device is offline"
            }
        }
    }

    fun releaseInventory(id: UUID) {
        viewModelScope.launch {
            when (val result = repository.releaseInventory(id)) {
                is NetworkState.Success -> _inventory.value = result.data
                is NetworkState.Error -> _error.value = "Failed to release: ${result.message}"
                is NetworkState.Loading -> {}
                is NetworkState.Offline -> _error.value = "Device is offline"
            }
        }
    }
}
