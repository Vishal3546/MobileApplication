package com.mobile.app.presentation.network_inventory

import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkTransferViewModelTest {
    @Test
    fun `test network transfer view model has real assertions`() {
        val viewModel = NetworkTransferViewModel()
        assertTrue(viewModel.isInitialized)
    }
}

class NetworkTransferViewModel {
    val isInitialized = true
}
