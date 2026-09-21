package com.mobile.app.presentation.inventory.list

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.core.security.TokenStorage
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.inventory.BrandSummary
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class GroupedUiState(
    val isLoading: Boolean = false,
    val groups: List<BrandSummary> = emptyList(),
    val error: String? = null
)

@OptIn(FlowPreview::class)
@HiltViewModel
class InventoryListViewModel @Inject constructor(
    private val repository: InventoryRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val filterState = MutableStateFlow(FilterState())

    /** Raw search input from the text field; debounced before it hits the API. */
    private val searchInput = MutableStateFlow("")

    private val _viewMode = MutableStateFlow(InventoryViewMode.FLAT)
    val viewMode: StateFlow<InventoryViewMode> = _viewMode.asStateFlow()

    private val _groupedState = MutableStateFlow(GroupedUiState())
    val groupedState: StateFlow<GroupedUiState> = _groupedState.asStateFlow()

    /** The committed (debounced) filters — screens use this so lazy brand expansions don't fire per keystroke. */
    val activeFilters: StateFlow<FilterState> = filterState.asStateFlow()

    init {
        // Automatically extract branchId from token for non-superadmins
        val branchId = getBranchIdFromToken()
        if (branchId != null) {
            filterState.value = filterState.value.copy(branchId = UUID.fromString(branchId))
        }

        // Debounced search: the flat paging flow refreshes reactively through filterState,
        // the grouped brand summaries are reloaded explicitly when in grouped mode.
        viewModelScope.launch {
            searchInput
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { query ->
                    filterState.value = filterState.value.copy(search = query.takeIf { it.isNotBlank() })
                    if (_viewMode.value == InventoryViewMode.GROUPED) {
                        loadGroupedSummary()
                    }
                }
        }
    }

    val inventoryPagingFlow: Flow<PagingData<Inventory>> = filterState.flatMapLatest { filter ->
        repository.getInventoryListPaging(
            status = filter.status,
            search = filter.search,
            branchId = filter.branchId
        ).cachedIn(viewModelScope)
    }

    fun updateSearch(query: String) {
        searchInput.value = query
    }

    fun updateStatus(status: String?) {
        filterState.value = filterState.value.copy(status = status)
        if (_viewMode.value == InventoryViewMode.GROUPED) {
            loadGroupedSummary()
        }
    }

    fun toggleViewMode() {
        _viewMode.value = if (_viewMode.value == InventoryViewMode.FLAT)
            InventoryViewMode.GROUPED else InventoryViewMode.FLAT

        if (_viewMode.value == InventoryViewMode.GROUPED) {
            loadGroupedSummary()
        }
    }

    fun loadGroupedSummary() {
        viewModelScope.launch {
            _groupedState.value = _groupedState.value.copy(isLoading = true, error = null)

            val filter = filterState.value
            when (val result = repository.getBrandWiseSummary(
                status = filter.status,
                search = filter.search,
                branchId = filter.branchId
            )) {
                is NetworkState.Success -> {
                    _groupedState.value = GroupedUiState(
                        isLoading = false,
                        groups = result.data.sortedByDescending { it.count }
                    )
                }
                is NetworkState.Error -> {
                    _groupedState.value = GroupedUiState(
                        isLoading = false,
                        error = result.message ?: "Failed to load brand summary"
                    )
                }
                is NetworkState.Offline -> {
                    _groupedState.value = GroupedUiState(
                        isLoading = false,
                        error = "No internet connection"
                    )
                }
                is NetworkState.Loading -> { }
            }
        }
    }

    private fun getBranchIdFromToken(): String? {
        val token = tokenStorage.getAccessToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                val regex = "\"branchId\":\"([^\"]+)\"".toRegex()
                regex.find(payload)?.groupValues?.get(1)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    data class FilterState(
        val search: String? = null,
        val status: String? = null,
        val branchId: UUID? = null
    )

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }
}
