package com.mobile.app.presentation.inventory.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.security.PermissionManager
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDetailScreen(
    inventoryId: String,
    onNavigateBack: () -> Unit,
    viewModel: InventoryDetailViewModel = hiltViewModel()
) {
    val inventory by viewModel.inventory.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(inventoryId) {
        viewModel.loadInventory(UUID.fromString(inventoryId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory Detail") },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            inventory?.let { inv ->
                Text("Stock Code: ${inv.stockCode}", style = MaterialTheme.typography.titleLarge)
                Text("Device: ${inv.brand} ${inv.model}")
                Text("IMEI: ${inv.imei}")
                Text("Status: ${inv.status}")
                Text("Selling Price: $${inv.sellingPrice}")

                Spacer(modifier = Modifier.height(16.dp))

                if (inv.status == "AVAILABLE") {
                    Button(onClick = { viewModel.reserveInventory(inv.id) }) {
                        Text("Reserve")
                    }
                }

                if (inv.status == "RESERVED") {
                    Button(onClick = { viewModel.releaseInventory(inv.id) }) {
                        Text("Release")
                    }
                }
            }
        }
    }
}
