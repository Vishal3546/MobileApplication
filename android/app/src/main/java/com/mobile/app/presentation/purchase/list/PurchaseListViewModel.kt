package com.mobile.app.presentation.purchase.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.domain.model.purchase.Purchase
import com.mobile.app.domain.repository.PurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class PurchaseListViewModel @Inject constructor(
    private val repository: PurchaseRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow<String?>(null)
    private val _statusFilter = MutableStateFlow<String?>(null)

    val purchases: Flow<PagingData<Purchase>> = _searchQuery
        .flatMapLatest { search ->
            repository.getPurchasesPaging(
                search = search,
                status = _statusFilter.value,
                startDate = null,
                endDate = null
            )
        }
        .cachedIn(viewModelScope)

    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
    }
}
