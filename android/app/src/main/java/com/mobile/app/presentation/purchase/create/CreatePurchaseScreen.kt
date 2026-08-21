package com.mobile.app.presentation.purchase.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.presentation.purchase.PurchaseWizardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePurchaseScreen(
    viewModel: PurchaseWizardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToWizard: (String) -> Unit
) {
    var customerId by remember { mutableStateOf("") }
    var deviceId by remember { mutableStateOf("") }
    var suggestedPrice by remember { mutableStateOf("") }
    var negotiatedPrice by remember { mutableStateOf("") }
    var finalPrice by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val wizardState by viewModel.wizardState.collectAsState()

    LaunchedEffect(wizardState.purchaseId) {
        if (wizardState.purchaseId != null) {
            onNavigateToWizard(wizardState.purchaseId!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Purchase") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = customerId,
                onValueChange = { customerId = it },
                label = { Text("Customer ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = deviceId,
                onValueChange = { deviceId = it },
                label = { Text("Device ID") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = suggestedPrice,
                onValueChange = { suggestedPrice = it },
                label = { Text("Suggested Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = negotiatedPrice,
                onValueChange = { negotiatedPrice = it },
                label = { Text("Negotiated Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = finalPrice,
                onValueChange = { finalPrice = it },
                label = { Text("Final Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.createPurchase(
                        customerId, 
                        deviceId, 
                        suggestedPrice.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO,
                        negotiatedPrice.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO,
                        finalPrice.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO,
                        notes
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !wizardState.isLoading
            ) {
                if (wizardState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Start Wizard")
                }
            }
            if (wizardState.error != null) {
                Text(text = wizardState.error!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
