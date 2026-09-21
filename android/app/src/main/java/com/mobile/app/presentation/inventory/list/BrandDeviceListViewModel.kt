package com.mobile.app.presentation.inventory.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BrandDeviceListViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val filterState = MutableStateFlow(BrandFilter())

    val brandInventoryPagingFlow: Flow<PagingData<Inventory>> = filterState.flatMapLatest { filter ->
        repository.getInventoryListPaging(
            status = filter.status,
            search = filter.brand,
            branchId = null
        ).cachedIn(viewModelScope)
    }

    fun loadBrand(brand: String, status: String?) {
        filterState.value = BrandFilter(brand = brand, status = status)
    }

    data class BrandFilter(
        val brand: String = "",
        val status: String? = null
    )
}
