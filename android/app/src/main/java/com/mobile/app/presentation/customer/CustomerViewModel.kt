package com.mobile.app.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.data.remote.dto.UpdateCustomerRequestDto
import com.mobile.app.domain.model.Customer
import com.mobile.app.domain.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class CustomerDetailState {
    object Loading : CustomerDetailState()
    data class Success(val customer: Customer) : CustomerDetailState()
    data class Error(val message: String) : CustomerDetailState()
}

sealed class CustomerActionState {
    object Idle : CustomerActionState()
    object Loading : CustomerActionState()
    object Success : CustomerActionState()
    data class Error(val message: String) : CustomerActionState()
}

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val customersPagingFlow: Flow<PagingData<Customer>> = _searchQuery
        .debounce(500L)
        .flatMapLatest { query ->
            val searchParam = query.takeIf { it.isNotBlank() }
            customerRepository.getCustomers(searchParam)
        }
        .cachedIn(viewModelScope)

    private val _detailState = MutableStateFlow<CustomerDetailState>(CustomerDetailState.Loading)
    val detailState: StateFlow<CustomerDetailState> = _detailState.asStateFlow()

    private val _actionState = MutableStateFlow<CustomerActionState>(CustomerActionState.Idle)
    val actionState: StateFlow<CustomerActionState> = _actionState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun loadCustomer(id: UUID) {
        _detailState.value = CustomerDetailState.Loading
        viewModelScope.launch {
            customerRepository.getCustomer(id)
                .onSuccess { _detailState.value = CustomerDetailState.Success(it) }
                .onFailure { _detailState.value = CustomerDetailState.Error(it.message ?: "Failed to load customer") }
        }
    }

    fun createCustomer(request: CreateCustomerRequestDto) {
        _actionState.value = CustomerActionState.Loading
        viewModelScope.launch {
            customerRepository.createCustomer(request)
                .onSuccess { _actionState.value = CustomerActionState.Success }
                .onFailure { _actionState.value = CustomerActionState.Error(it.message ?: "Failed to create customer") }
        }
    }

    fun updateCustomer(id: UUID, request: UpdateCustomerRequestDto) {
        _actionState.value = CustomerActionState.Loading
        viewModelScope.launch {
            customerRepository.updateCustomer(id, request)
                .onSuccess { 
                    _actionState.value = CustomerActionState.Success
                    loadCustomer(id) // Reload details
                }
                .onFailure { _actionState.value = CustomerActionState.Error(it.message ?: "Failed to update customer") }
        }
    }

    fun updateCustomerStatus(id: UUID, status: String) {
        _actionState.value = CustomerActionState.Loading
        viewModelScope.launch {
            customerRepository.updateCustomerStatus(id, status)
                .onSuccess {
                    _actionState.value = CustomerActionState.Success
                    loadCustomer(id) // Reload details
                }
                .onFailure { _actionState.value = CustomerActionState.Error(it.message ?: "Failed to update status") }
        }
    }
    
    fun resetActionState() {
        _actionState.value = CustomerActionState.Idle
    }
}
