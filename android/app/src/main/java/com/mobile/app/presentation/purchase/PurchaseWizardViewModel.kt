package com.mobile.app.presentation.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.domain.model.purchase.Purchase
import com.mobile.app.domain.model.purchase.PurchaseStatus
import com.mobile.app.domain.model.device.*
import com.mobile.app.domain.repository.PurchaseRepository
import com.mobile.app.domain.repository.device.DeviceRepository
import com.mobile.app.domain.repository.device.DeviceInspectionRepository
import com.mobile.app.domain.repository.device.DeviceConditionRepository
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
    
    // Step 2: Inspection
    val inspectionCreate: DeviceInspectionCreate? = null,
    
    // Step 3: Condition
    val conditionCreate: DeviceConditionCreate? = null,
    
    // Step 4: Valuation
    val suggestedPrice: BigDecimal = BigDecimal.ZERO,
    val negotiatedPrice: BigDecimal = BigDecimal.ZERO,
    val finalPrice: BigDecimal = BigDecimal.ZERO,
    
    // Step 5: Customer
    val customerId: String? = null,
    
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
    private val inspectionRepository: DeviceInspectionRepository,
    private val conditionRepository: DeviceConditionRepository
) : ViewModel() {
    private val _wizardState = MutableStateFlow(WizardState())
    val wizardState: StateFlow<WizardState> = _wizardState

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
                    _wizardState.value = _wizardState.value.copy(
                        deviceId = createdDevice.id,
                        deviceCreate = device,
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

    // --- Step 2: Inspection ---
    fun submitInspection(inspection: DeviceInspectionCreate) {
        val deviceId = _wizardState.value.deviceId ?: return
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = inspectionRepository.createInspection(deviceId, inspection)
            result.fold(
                onSuccess = {
                    _wizardState.value = _wizardState.value.copy(
                        inspectionCreate = inspection,
                        currentStep = 3,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _wizardState.value = _wizardState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    // --- Step 3: Condition ---
    fun submitCondition(condition: DeviceConditionCreate) {
        val deviceId = _wizardState.value.deviceId ?: return
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoading = true, error = null)
            val result = conditionRepository.createCondition(deviceId, condition)
            result.fold(
                onSuccess = {
                    _wizardState.value = _wizardState.value.copy(
                        conditionCreate = condition,
                        currentStep = 4,
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    _wizardState.value = _wizardState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }

    // --- Step 4: Valuation ---
    fun setPrices(suggested: BigDecimal, negotiated: BigDecimal, final: BigDecimal) {
        _wizardState.value = _wizardState.value.copy(
            suggestedPrice = suggested,
            negotiatedPrice = negotiated,
            finalPrice = final,
            currentStep = 5
        )
    }

    // --- Step 5: Customer & Final Purchase ---
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
                    currentStep = 6,
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
