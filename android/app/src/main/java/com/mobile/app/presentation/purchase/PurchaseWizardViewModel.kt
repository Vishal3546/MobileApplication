package com.mobile.app.presentation.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.purchase.Purchase
import com.mobile.app.domain.model.purchase.PurchaseStatus
import com.mobile.app.domain.model.device.*
import com.mobile.app.domain.repository.PurchaseRepository
import com.mobile.app.domain.repository.device.DeviceRepository
import com.mobile.app.domain.repository.PhoneSpecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

data class WizardState(
    val currentStep: Int = 1,
    val deviceId: String? = null,
    val purchaseId: String? = null,
    val currentPurchase: Purchase? = null,
    
    // Step 1: Device Info
    val deviceCreate: DeviceCreate? = null,
    
    // Step 2: Pricing (simple hisab — no tests)
    val suggestedPrice: BigDecimal = BigDecimal.ZERO,
    val negotiatedPrice: BigDecimal = BigDecimal.ZERO,
    val finalPrice: BigDecimal = BigDecimal.ZERO,
    
    // Step 3: Customer
    val customerId: String? = null,
    
    // Pricing Breakdown
    val pricingBreakdown: PricingBreakdown? = null,
    
    // Auto-fill data
    val fetchedDeviceDetails: Device? = null,
    val isFetchingImei: Boolean = false,
    
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class PurchaseWizardViewModel @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    private val deviceRepository: DeviceRepository,
    private val phoneSpecsRepository: PhoneSpecsRepository
) : ViewModel() {
    private val _wizardState = MutableStateFlow(WizardState())
    val wizardState: StateFlow<WizardState> = _wizardState

    private val _models = MutableStateFlow<List<ModelInfo>>(emptyList())
    val models: StateFlow<List<ModelInfo>> = _models

    private val _isFetchingModels = MutableStateFlow(false)
    val isFetchingModels: StateFlow<Boolean> = _isFetchingModels

    fun loadModelsForBrand(brand: String) {
        _models.value = emptyList()
        _isFetchingModels.value = true
        viewModelScope.launch {
            val result = phoneSpecsRepository.getModelsForBrand(brand)
            result.onSuccess { liveModels ->
                if (liveModels.isNotEmpty()) {
                    _models.value = liveModels
                }
            }
            _isFetchingModels.value = false
        }
    }

    fun nextStep() {
        _wizardState.value = _wizardState.value.copy(currentStep = _wizardState.value.currentStep + 1)
    }

    fun previousStep() {
        if (_wizardState.value.currentStep > 1) {
            _wizardState.value = _wizardState.value.copy(currentStep = _wizardState.value.currentStep - 1)
        }
    }

    // --- IMEI Lookup ---
    fun fetchDeviceDetails(imei: String) {
        if (imei.length < 15) return
        
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isFetchingImei = true, error = null)
            val result = deviceRepository.getDeviceInfoByImei(imei)
            result.fold(
                onSuccess = { device ->
                    _wizardState.value = _wizardState.value.copy(
                        fetchedDeviceDetails = device,
                        isFetchingImei = false
                    )
                },
                onFailure = { e ->
                    _wizardState.value = _wizardState.value.copy(
                        isFetchingImei = false,
                        error = "Could not find device for this IMEI: ${e.message}"
                    )
                }
            )
        }
    }

    // --- Step 1: Device Creation ---
    fun submitDeviceInfo(device: DeviceCreate) {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = deviceRepository.createDevice(device)
            result.fold(
                onSuccess = { createdDevice ->
                    // No test/inspection steps any more: price straight from
                    // brand/model base price, owner adjusts in the next step.
                    val breakdown = PricingCalculator.calculatePrice(
                        brand = device.brand,
                        model = device.model,
                        inspection = null,
                        condition = null
                    )
                    _wizardState.value = _wizardState.value.copy(
                        deviceId = createdDevice.id,
                        deviceCreate = device,
                        pricingBreakdown = breakdown,
                        suggestedPrice = breakdown.basePrice,
                        finalPrice = breakdown.finalPrice,
                        currentStep = 2,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _wizardState.value = _wizardState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    // --- Step 2: Pricing ---
    fun setPrices(suggested: BigDecimal, negotiated: BigDecimal, final: BigDecimal) {
        _wizardState.value = _wizardState.value.copy(
            suggestedPrice = suggested,
            negotiatedPrice = negotiated,
            finalPrice = final,
            currentStep = 3
        )
    }

    // --- Step 3: Customer & Final Purchase ---
    fun createFinalPurchase(customerId: String, notes: String?) {
        val deviceId = _wizardState.value.deviceId ?: return
        val state = _wizardState.value
        
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = purchaseRepository.createPurchase(
                customerId = customerId,
                deviceId = deviceId,
                suggestedPrice = state.suggestedPrice,
                negotiatedPrice = state.negotiatedPrice,
                finalPrice = state.finalPrice,
                notes = notes
            )
            result.onSuccess { purchase ->
                _wizardState.value = _wizardState.value.copy(
                    purchaseId = purchase.id,
                    currentPurchase = purchase,
                    currentStep = 4,
                    isLoading = false
                )
            }.onFailure { e ->
                _wizardState.value = _wizardState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun completePurchase() {
        val id = _wizardState.value.purchaseId ?: return
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = purchaseRepository.completePurchase(id)
            result.onSuccess { purchase ->
                _wizardState.value = _wizardState.value.copy(
                    currentPurchase = purchase,
                    isLoading = false,
                    isComplete = true
                )
            }.onFailure { e ->
                _wizardState.value = _wizardState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
