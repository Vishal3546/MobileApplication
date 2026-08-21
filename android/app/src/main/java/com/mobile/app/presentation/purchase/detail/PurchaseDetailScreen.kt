package com.mobile.app.presentation.purchase.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseDetailScreen(
    purchaseId: String,
    viewModel: PurchaseDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val purchase by viewModel.purchase.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(purchaseId) {
        viewModel.loadPurchase(purchaseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Purchase Detail") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else if (purchase != null) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Purchase Number: ${purchase!!.purchaseNumber}", style = MaterialTheme.typography.titleLarge)
                    Text("Status: ${purchase!!.status}")
                    Text("Customer: ${purchase!!.customer?.firstName ?: purchase!!.customerId}")
                    Text("Device: ${purchase!!.device?.brand ?: purchase!!.deviceId}")
                    Text("Final Price: ₹${purchase!!.finalPrice}")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (purchase!!.status != com.mobile.app.domain.model.purchase.PurchaseStatus.COMPLETED && purchase!!.status != com.mobile.app.domain.model.purchase.PurchaseStatus.CANCELLED) {
                        Button(onClick = { viewModel.cancelPurchase(purchaseId, "User requested") }, modifier = Modifier.fillMaxWidth()) {
                            Text("Cancel Purchase")
                        }
                    }
                }
            }
        }
    }
}
