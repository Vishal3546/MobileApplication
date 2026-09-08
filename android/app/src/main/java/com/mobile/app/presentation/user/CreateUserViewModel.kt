package com.mobile.app.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.data.remote.dto.user.CreateUserRequestDto
import com.mobile.app.data.remote.dto.user.RoleResponseDto
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.shop.Shop
import com.mobile.app.domain.repository.ShopRepository
import com.mobile.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateUserState {
    object Idle : CreateUserState()
    object Loading : CreateUserState()
    object Success : CreateUserState()
    data class Error(val message: String) : CreateUserState()
}

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _createUserState = MutableStateFlow<CreateUserState>(CreateUserState.Idle)
    val createUserState: StateFlow<CreateUserState> = _createUserState.asStateFlow()

    private val _roles = MutableStateFlow<List<RoleResponseDto>>(emptyList())
    val roles: StateFlow<List<RoleResponseDto>> = _roles.asStateFlow()

    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    init {
        fetchRoles()
        fetchShops()
    }

    private fun fetchRoles() {
        viewModelScope.launch {
            when (val result = userRepository.getRoles()) {
                is NetworkState.Success -> _roles.value = result.data
                else -> { /* Handle error or retry */ }
            }
        }
    }

    private fun fetchShops() {
        viewModelScope.launch {
            when (val result = shopRepository.getShops()) {
                is NetworkState.Success -> _shops.value = result.data
                else -> { /* Handle error or retry */ }
            }
        }
    }

    fun createUser(request: CreateUserRequestDto) {
        viewModelScope.launch {
            _createUserState.value = CreateUserState.Loading
            when (val result = userRepository.createUser(request)) {
                is NetworkState.Success -> _createUserState.value = CreateUserState.Success
                is NetworkState.Error -> _createUserState.value = CreateUserState.Error(result.message ?: "Failed to create user")
                is NetworkState.Offline -> _createUserState.value = CreateUserState.Error("No internet connection")
                is NetworkState.Loading -> { }
            }
        }
    }

    fun resetCreateState() {
        _createUserState.value = CreateUserState.Idle
    }
}
