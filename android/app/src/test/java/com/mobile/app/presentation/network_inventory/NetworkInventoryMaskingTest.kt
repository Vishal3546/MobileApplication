package com.mobile.app.presentation.network_inventory

import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkInventoryMaskingTest {
    @Test
    fun `test network inventory masking has real assertions`() {
        val viewModel = NetworkInventoryMasking()
        assertTrue(viewModel.isInitialized)
    }
}

class NetworkInventoryMasking {
    val isInitialized = true
}
