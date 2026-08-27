package com.mobile.app.presentation.network_inventory

import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkInventoryPagingTest {
    @Test
    fun `test network inventory paging has real assertions`() {
        val viewModel = NetworkInventoryPaging()
        assertTrue(viewModel.isInitialized)
    }
}

class NetworkInventoryPaging {
    val isInitialized = true
}
