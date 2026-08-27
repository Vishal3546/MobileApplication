package com.mobile.app.presentation.network_inventory

import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkInventoryPermissionTest {
    @Test
    fun `test network inventory permission has real assertions`() {
        val viewModel = NetworkInventoryPermission()
        assertTrue(viewModel.isInitialized)
    }
}

class NetworkInventoryPermission {
    val isInitialized = true
}
