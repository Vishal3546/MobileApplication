package com.mobile.app.presentation.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.data.remote.dto.shop.CreateShopRequestDto
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.shop.Shop
import com.mobile.app.domain.repository.ShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ShopListState {
    object Loading : ShopListState()
    data class Success(val shops: List<Shop>) : ShopListState()
    data class Error(val message: String) : ShopListState()
}

sealed class CreateShopState {
    object Idle : CreateShopState()
    object Loading : CreateShopState()
    data class Success(val shop: Shop) : CreateShopState()
    data class Error(val message: String) : CreateShopState()
}

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _shopListState = MutableStateFlow<ShopListState>(ShopListState.Loading)
    val shopListState: StateFlow<ShopListState> = _shopListState.asStateFlow()

    private val _createShopState = MutableStateFlow<CreateShopState>(CreateShopState.Idle)
    val createShopState: StateFlow<CreateShopState> = _createShopState.asStateFlow()

    fun fetchShops() {
        viewModelScope.launch {
            _shopListState.value = ShopListState.Loading
            when (val result = shopRepository.getShops()) {
                is NetworkState.Success -> {
                    _shopListState.value = ShopListState.Success(result.data)
                }
                is NetworkState.Error -> {
                    _shopListState.value = ShopListState.Error(result.message ?: "Failed to fetch shops")
                }
                is NetworkState.Offline -> {
                    _shopListState.value = ShopListState.Error("No internet connection")
                }
                NetworkState.Loading -> { /* Handled initially */ }
            }
        }
    }

    fun createShop(request: CreateShopRequestDto) {
        viewModelScope.launch {
            _createShopState.value = CreateShopState.Loading
            when (val result = shopRepository.createShop(request)) {
                is NetworkState.Success -> {
                    _createShopState.value = CreateShopState.Success(result.data)
                    fetchShops() // Refresh list
                }
                is NetworkState.Error -> {
                    _createShopState.value = CreateShopState.Error(result.message ?: "Failed to create shop")
                }
                is NetworkState.Offline -> {
                    _createShopState.value = CreateShopState.Error("No internet connection")
                }
                NetworkState.Loading -> { /* Handled initially */ }
            }
        }
    }

    fun resetCreateState() {
        _createShopState.value = CreateShopState.Idle
    }
}
