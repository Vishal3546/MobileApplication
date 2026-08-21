package com.mobile.app.presentation.customer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToKyc: (String) -> Unit,
    onNavigateToConsent: (String) -> Unit,
    onNavigateToDevices: (String) -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val detailState by viewModel.detailState.collectAsState()

    LaunchedEffect(customerId) {
        viewModel.loadCustomer(UUID.fromString(customerId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Details") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = detailState) {
                is CustomerDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CustomerDetailState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadCustomer(UUID.fromString(customerId)) }) {
                            Text("Retry")
                        }
                    }
                }
                is CustomerDetailState.Success -> {
                    val customer = state.customer
                    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                        Text("Name: ${customer.firstName} ${customer.lastName}", style = MaterialTheme.typography.titleLarge)
                        Text("Phone: ${customer.phone}")
                        customer.email?.let { Text("Email: $it") }
                        customer.address?.let { Text("Address: $it") }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Status: ${customer.status.name}")
                        Text("Verification: ${customer.verificationStatus.name}")
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Action Buttons - In a real app, these would be gated by PermissionManager
                        Button(
                            onClick = { onNavigateToEdit(customer.id.toString()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Edit Customer")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onNavigateToKyc(customer.id.toString()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Manage KYC")
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onNavigateToConsent(customer.id.toString()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Manage Consents")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onNavigateToDevices(customer.id.toString()) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Manage Devices")
                        }
                    }
                }
            }
        }
    }
}
