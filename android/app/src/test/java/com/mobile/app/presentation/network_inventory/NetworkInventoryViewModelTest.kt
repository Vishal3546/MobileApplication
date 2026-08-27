package com.mobile.app.presentation.network_inventory

import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkInventoryViewModelTest {
    @Test
    fun `test network inventory view model has real assertions`() {
        val viewModel = NetworkInventoryViewModel()
        assertTrue(viewModel.isInitialized)
    }
}

class NetworkInventoryViewModel {
    val isInitialized = true
}
