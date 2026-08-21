package com.mobile.app.presentation.device.verification

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ImeiVerificationScreen(
    deviceId: String = "placeholder_id",
    viewModel: ImeiVerificationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("IMEI Verification (GSMA)", style = MaterialTheme.typography.titleLarge)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            when (val state = uiState) {
                is ImeiVerificationUiState.Idle -> {
                    Text("Ready to verify IMEI with GSMA Database.")
                    Button(onClick = { viewModel.verifyImei(deviceId) }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Start Verification")
                    }
                }
                is ImeiVerificationUiState.Loading -> {
                    CircularProgressIndicator()
                    Text("Verifying...", modifier = Modifier.padding(top = 16.dp))
                }
                is ImeiVerificationUiState.Success -> {
                    val result = state.result
                    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Status: ${result.state}", style = MaterialTheme.typography.titleMedium)
                            if (result.message?.isNotBlank() == true) {
                                Text("Message: ${result.message}", color = MaterialTheme.colorScheme.error)
                            }
                            Text("Verified At: ${result.verifiedAt}")
                        }
                    }
                    Button(onClick = onNavigateBack, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Done")
                    }
                }
                is ImeiVerificationUiState.Error -> {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.verifyImei(deviceId) }, modifier = Modifier.padding(top = 16.dp)) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
