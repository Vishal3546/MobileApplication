package com.mobile.app.presentation.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.app.core.security.TokenStorage
import com.mobile.app.data.remote.dto.reports.InventoryReportResponse
import com.mobile.app.data.remote.dto.reports.PurchaseReportResponse
import com.mobile.app.data.remote.dto.reports.SalesReportResponse
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ReportsUiState(
    val sales: SalesReportResponse? = null,
    val purchases: PurchaseReportResponse? = null,
    val inventory: InventoryReportResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val repository: ReportRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private var currentRange = "DAILY"

    init {
        fetchReports("DAILY")
    }

    fun fetchReports(range: String) {
        currentRange = range
        val branchId = getBranchIdFromToken()?.let { UUID.fromString(it) }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            val salesResult = repository.getSalesReport(branchId, range)
            val purchaseResult = repository.getPurchaseReport(branchId, range)
            val inventoryResult = repository.getInventoryReport(branchId)

            if (salesResult is NetworkState.Success && purchaseResult is NetworkState.Success && inventoryResult is NetworkState.Success) {
                _uiState.value = _uiState.value.copy(
                    sales = salesResult.data,
                    purchases = purchaseResult.data,
                    inventory = inventoryResult.data,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load reports. Please try again."
                )
            }
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
