package com.mobile.app.presentation.inventory.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InventoryListViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val filterState = MutableStateFlow(FilterState())

    val inventoryPagingFlow: Flow<PagingData<Inventory>> = filterState.flatMapLatest { filter ->
        repository.getInventoryListPaging(
            status = filter.status,
            search = filter.search,
            branchId = filter.branchId
        ).cachedIn(viewModelScope)
    }

    fun updateSearch(query: String) {
        filterState.value = filterState.value.copy(search = query.takeIf { it.isNotBlank() })
    }

    fun updateStatus(status: String?) {
        filterState.value = filterState.value.copy(status = status)
    }

    data class FilterState(
        val search: String? = null,
        val status: String? = null,
        val branchId: UUID? = null
    )
}
