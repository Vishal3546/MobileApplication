package com.mobile.app.presentation.sales.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.domain.model.sales.SaleTransaction
import com.mobile.app.domain.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SaleListViewModel @Inject constructor(
    private val repository: SaleRepository
) : ViewModel() {

    private val filterState = MutableStateFlow(FilterState())

    val salesPagingFlow: Flow<PagingData<SaleTransaction>> = filterState.flatMapLatest { filter ->
        repository.getSalesPaging(
            status = filter.status,
            paymentStatus = filter.paymentStatus,
            search = filter.search
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
        val paymentStatus: String? = null
    )
}
