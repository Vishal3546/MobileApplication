package com.mobile.app.presentation.inventory.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mobile.app.core.security.TokenStorage
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.domain.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class InventoryListViewModel @Inject constructor(
    private val repository: InventoryRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val filterState = MutableStateFlow(FilterState())

    init {
        // Automatically extract branchId from token for non-superadmins
        val branchId = getBranchIdFromToken()
        if (branchId != null) {
            filterState.value = filterState.value.copy(branchId = UUID.fromString(branchId))
        }
    }

    val inventoryPagingFlow: Flow<PagingData<Inventory>> = filterState.flatMapLatest { filter ->
        repository.getInventoryListPaging(
            status = filter.status,
            search = filter.search,
            branchId = filter.branchId
        ).cachedIn(viewModelScope)
    }

    fun updateSearch(query: String) {
        filterState.value = filterState.value.copy(search = query.takeIf { it.isNotBlank() })
    }

    fun updateStatus(status: String?) {
        filterState.value = filterState.value.copy(status = status)
    }

    private fun getBranchIdFromToken(): String? {
        val token = tokenStorage.getAccessToken() ?: return null
        return try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
                // Look for branchId in JWT payload
                val regex = "\"branchId\":\"([^\"]+)\"".toRegex()
                regex.find(payload)?.groupValues?.get(1)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    data class FilterState(
        val search: String? = null,
        val status: String? = null,
        val branchId: UUID? = null
    )
}
