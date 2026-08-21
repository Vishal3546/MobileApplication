package com.mobile.app.presentation.device.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.repository.device.DeviceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _brandFilter = MutableStateFlow<String?>(null)
    val brandFilter: StateFlow<String?> = _brandFilter.asStateFlow()

    private val _modelFilter = MutableStateFlow<String?>(null)
    val modelFilter: StateFlow<String?> = _modelFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    private val filterTrigger = MutableStateFlow(FilterState())

    val devices: Flow<PagingData<Device>> = filterTrigger
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { filter ->
            repository.getDevices(
                search = filter.search.takeIf { it.isNotBlank() },
                brand = filter.brand,
                model = filter.model,
                status = filter.status
            )
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        updateFilter()
    }

    fun onBrandFilterChanged(brand: String?) {
        _brandFilter.value = brand
        updateFilter()
    }

    fun onModelFilterChanged(model: String?) {
        _modelFilter.value = model
        updateFilter()
    }

    fun onStatusFilterChanged(status: String?) {
        _statusFilter.value = status
        updateFilter()
    }

    private fun updateFilter() {
        filterTrigger.value = FilterState(
            search = _searchQuery.value,
            brand = _brandFilter.value,
            model = _modelFilter.value,
            status = _statusFilter.value
        )
    }

    private data class FilterState(
        val search: String = "",
        val brand: String? = null,
        val model: String? = null,
        val status: String? = null
    )
}
