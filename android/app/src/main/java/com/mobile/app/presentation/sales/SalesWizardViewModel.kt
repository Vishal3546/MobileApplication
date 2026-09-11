package com.mobile.app.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.core.security.TokenStorage
import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.model.sales.SaleTransaction
import com.mobile.app.domain.model.shop.Shop
import com.mobile.app.domain.repository.ShopRepository
import com.mobile.app.domain.repository.CustomerRepository
import com.mobile.app.domain.repository.InventoryRepository
import com.mobile.app.domain.repository.SaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

data class SalesWizardState(
    val currentStep: Int = 1,
    val selectedInventory: Inventory? = null,
    val customerId: UUID? = null,
    val saleTransaction: SaleTransaction? = null,
    
    // Admin Support
    val isSuperAdmin: Boolean = false,
    val shops: List<Shop> = emptyList(),
    val selectedBranchId: UUID? = null,
    
    val basePrice: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val finalPrice: BigDecimal = BigDecimal.ZERO,
    
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class SalesWizardViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val customerRepository: CustomerRepository,
    private val saleRepository: SaleRepository,
    private val shopRepository: ShopRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesWizardState())
    val uiState: StateFlow<SalesWizardState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
    
    // Trigger refresh when search OR selectedBranchId changes
    val availableInventory: Flow<PagingData<Inventory>> = kotlinx.coroutines.flow.combine(
        _searchQuery,
        _uiState.map { it.selectedBranchId }.distinctUntilChanged()
    ) { query, branchId ->
        Pair(query, branchId)
    }.flatMapLatest { (query, branchId) ->
        inventoryRepository.getInventoryListPaging(
            status = "AVAILABLE",
            search = query.takeIf { it.isNotBlank() },
            branchId = branchId
        )
    }.cachedIn(viewModelScope)

    init {
        checkAdminStatus()
        // Automatically extract branchId from token for non-superadmins
        val branchId = getBranchIdFromToken()
        if (branchId != null) {
            _uiState.value = _uiState.value.copy(selectedBranchId = UUID.fromString(branchId))
        }
    }

    private fun checkAdminStatus() {
        val token = tokenStorage.getAccessToken() ?: return
        val isSuper = token.contains("SUPER_ADMIN") // Simple check for now
        if (isSuper) {
            _uiState.value = _uiState.value.copy(isSuperAdmin = true)
            fetchShops()
        }
    }

    private fun fetchShops() {
        viewModelScope.launch {
            when (val result = shopRepository.getShops()) {
                is NetworkState.Success -> _uiState.value = _uiState.value.copy(shops = result.data)
                else -> {}
            }
        }
    }

    fun selectBranch(id: UUID) {
        _uiState.value = _uiState.value.copy(selectedBranchId = id)
    }

    fun selectInventory(inventory: Inventory) {
        _uiState.value = _uiState.value.copy(
            selectedInventory = inventory,
            basePrice = inventory.sellingPrice,
            finalPrice = inventory.sellingPrice,
            currentStep = 2
        )
    }

    fun setDiscount(amount: BigDecimal) {
        val base = _uiState.value.basePrice
        val final = base.subtract(amount)
        _uiState.value = _uiState.value.copy(
            discountAmount = amount,
            finalPrice = final
        )
    }

    fun createCustomerAndSale(firstName: String, lastName: String, phone: String, email: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // 1. Create Customer
            val customerResult = customerRepository.createCustomer(
                CreateCustomerRequestDto(firstName, lastName, phone, null, email, null)
            )
            
            if (customerResult.isSuccess) {
                val customerId = customerResult.getOrNull()?.id ?: return@launch
                val inventoryId = _uiState.value.selectedInventory?.id ?: return@launch
                val branchId = UUID.fromString(getBranchIdFromToken() ?: return@launch)
                
                // 2. Create Sale
                val saleResult = saleRepository.createSale(customerId, inventoryId, branchId)
                
                if (saleResult is NetworkState.Success) {
                    var sale = saleResult.data
                    
                    // 3. Apply Discount if any
                    if (_uiState.value.discountAmount > BigDecimal.ZERO) {
                        val overrideResult = saleRepository.overridePrice(
                            sale.id, 
                            _uiState.value.finalPrice, 
                            "Wizard Discount"
                        )
                        if (overrideResult is NetworkState.Success) {
                            sale = overrideResult.data
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        customerId = customerId,
                        saleTransaction = sale,
                        currentStep = 3,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to create sale transaction")
                }
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to create customer")
            }
        }
    }

    fun completeSale(paymentMode: String) {
        val saleId = _uiState.value.saleTransaction?.id ?: return
        val amount = _uiState.value.finalPrice
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            // 1. Create Payment
            val paymentResult = saleRepository.createPayment(
                saleId = saleId,
                paymentMode = paymentMode,
                amount = amount,
                referenceNumber = null,
                idempotencyKey = UUID.randomUUID().toString()
            )
            
            if (paymentResult is NetworkState.Success) {
                // 2. Complete Sale
                val completeResult = saleRepository.completeSale(saleId)
                if (completeResult is NetworkState.Success) {
                    _uiState.value = _uiState.value.copy(
                        saleTransaction = completeResult.data,
                        currentStep = 4,
                        isLoading = false,
                        isComplete = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Failed to finalize sale")
                }
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Payment recording failed")
            }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    private fun getBranchIdFromToken(): String? {
        val token = tokenStorage.getAccessToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                val regex = "\"branchId\":\"([^\"]+)\"".toRegex()
                regex.find(payload)?.groupValues?.get(1)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
