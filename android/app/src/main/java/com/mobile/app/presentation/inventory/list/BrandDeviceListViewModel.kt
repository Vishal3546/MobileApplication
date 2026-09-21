package com.mobile.app.presentation.inventory.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Backs the device list of a single expanded brand accordion in the grouped
 * inventory view. One instance per brand (created with a keyed hiltViewModel),
 * so every brand keeps its own cached result while the screen is open.
 */
@HiltViewModel
class BrandDeviceListViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    data class BrandDevicesUiState(
        val isLoading: Boolean = false,
        val devices: List<Inventory> = emptyList(),
        val totalCount: Long = 0L,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(BrandDevicesUiState())
    val uiState: StateFlow<BrandDevicesUiState> = _uiState.asStateFlow()

    /** Identifies the last successful request so expand/collapse doesn't refetch. */
    private var loadedKey: String? = null

    /**
     * Lazily loads the devices of one brand (called when its accordion is expanded).
     *
     * If a global search query is active it takes precedence over the brand name so the
     * expanded list stays consistent with the (already filtered) brand summary counts.
     */
    fun loadDevices(brand: String, status: String?, search: String?) {
        val effectiveSearch = search?.takeIf { it.isNotBlank() } ?: brand
        val requestKey = "$brand::$status::$effectiveSearch"

        if (requestKey == loadedKey && _uiState.value.error == null) return
        loadedKey = requestKey

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = repository.getInventoryItems(
                page = 0,
                size = BRAND_PAGE_SIZE,
                status = status,
                search = effectiveSearch,
                branchId = null // backend scopes to the current user's branch automatically
            )) {
                is NetworkState.Success -> {
                    _uiState.value = BrandDevicesUiState(
                        isLoading = false,
                        devices = result.data.items,
                        totalCount = result.data.totalElements,
                        error = null
                    )
                }
                is NetworkState.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message ?: "Failed to load devices"
                    )
                }
                is NetworkState.Offline -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No internet connection"
                    )
                }
                is NetworkState.Loading -> Unit
            }
        }
    }

    companion object {
        /** Brands rarely exceed a hundred units in a shop; the UI shows a "switch to flat view" hint beyond this. */
        private const val BRAND_PAGE_SIZE = 100
    }
}
