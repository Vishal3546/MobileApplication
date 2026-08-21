package com.mobile.app.presentation.purchase.steps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchasePaymentScreen(
    purchaseId: String,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Payment") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Payment Screen placeholder")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onPaymentSuccess) {
                Text("Simulate Payment Success")
            }
        }
    }
}
