package com.mobile.app.presentation.consent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.data.remote.dto.CaptureConsentRequestDto
import com.mobile.app.domain.model.Consent
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsentScreen(
    customerId: String,
    onNavigateBack: () -> Unit,
    onNavigateToSignature: (String) -> Unit,
    viewModel: ConsentViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var consentType by remember { mutableStateOf("KYC_CONSENT") }
    var consentTextVersion by remember { mutableStateOf("v1.0") }
    var confirmed by remember { mutableStateOf(false) }

    LaunchedEffect(customerId) {
        viewModel.loadConsents(UUID.fromString(customerId))
    }

    LaunchedEffect(actionState) {
        if (actionState is ConsentActionState.Success) {
            viewModel.resetActionState()
            confirmed = false
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Customer Consents") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            // Capture New Consent Section
            Card(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Capture New Consent", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = consentType,
                        onValueChange = { consentType = it },
                        label = { Text("Consent Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = confirmed,
                            onCheckedChange = { confirmed = it }
                        )
                        Text("Customer explicitly confirms consent to $consentTextVersion")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onNavigateToSignature(customerId) },
                            modifier = Modifier.weight(1f)
                        ) { Text("Capture Signature") }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (actionState is ConsentActionState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        Button(
                            onClick = {
                                viewModel.captureConsent(
                                    customerId = UUID.fromString(customerId),
                                    request = CaptureConsentRequestDto(
                                        consentType = consentType,
                                        consentTextVersion = consentTextVersion,
                                        signatureMediaId = null, // Will come from Signature flow
                                        videoMediaId = null,
                                        ipAddress = "127.0.0.1",
                                        deviceInfo = "Android App"
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = confirmed
                        ) {
                            Text("Submit Consent")
                        }
                    }
                    
                    if (actionState is ConsentActionState.Error) {
                        Text((actionState as ConsentActionState.Error).message, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Divider()

            // Consent History Section
            when (val state = listState) {
                is ConsentListState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is ConsentListState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
                is ConsentListState.Success -> {
                    LazyColumn(modifier = Modifier.weight(1f).padding(16.dp)) {
                        item { Text("Consent History", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp)) }
                        items(state.consents) { consent ->
                            ConsentCard(consent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsentCard(consent: Consent) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Type: ${consent.consentType.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Version: ${consent.consentTextVersion}", style = MaterialTheme.typography.bodyMedium)
            Text("Captured: ${consent.capturedAt}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
