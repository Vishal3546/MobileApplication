package com.mobile.app.presentation.sales.detail

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
fun SaleDetailScreen(
    saleId: String,
    onNavigateBack: () -> Unit,
    viewModel: SaleDetailViewModel = hiltViewModel()
) {
    val sale by viewModel.sale.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(saleId) {
        viewModel.loadSale(UUID.fromString(saleId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sale Detail") },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }

            sale?.let { s ->
                Text("Sale Number: ${s.saleNumber}", style = MaterialTheme.typography.titleLarge)
                Text("Status: ${s.saleStatus}")
                Text("Payment Status: ${s.paymentStatus}")
                Text("Selling Price: $${s.sellingPrice}")
                Text("Discount: $${s.discount}")
                Text("Tax: $${s.tax}")
                Text("Final Amount: $${s.finalAmount}")

                Spacer(modifier = Modifier.height(16.dp))

                if (s.saleStatus != "CANCELLED" && s.saleStatus != "COMPLETED") {
                    Button(onClick = { viewModel.cancelSale(s.id, "Cancelled by user") }) {
                        Text("Cancel Sale")
                    }
                }
            }
        }
    }
}
