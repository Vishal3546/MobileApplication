package com.mobile.app.presentation.kyc

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
fun KycDocumentDetailScreen(
    customerId: String,
    documentId: String,
    onNavigateBack: () -> Unit,
    viewModel: KycViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    var notes by remember { mutableStateOf("") }

    LaunchedEffect(actionState) {
        if (actionState is KycActionState.Success) {
            viewModel.resetActionState()
            onNavigateBack() // Or just reload the detail
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("KYC Document Detail") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = listState) {
                is KycListState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is KycListState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is KycListState.Success -> {
                    val doc = state.documents.find { it.id.toString() == documentId }
                    if (doc == null) {
                        Text("Document not found", modifier = Modifier.align(Alignment.Center))
                    } else {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                            Text("Type: ${doc.idType.name}", style = MaterialTheme.typography.titleLarge)
                            Text("Masked ID: ${doc.idNumberMasked}")
                            Text("Status: ${doc.verificationStatus.name}")
                            doc.verificationNotes?.let { Text("Notes: $it") }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Media preview placeholders...")

                            Spacer(modifier = Modifier.height(32.dp))
                            
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Approval/Rejection Notes") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (actionState is KycActionState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            } else {
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        onClick = { viewModel.approveDocument(doc.customerId, doc.id, notes.takeIf { it.isNotBlank() }) },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Approve") }
                                    Button(
                                        onClick = { viewModel.rejectDocument(doc.customerId, doc.id, notes.takeIf { it.isNotBlank() }) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) { Text("Reject") }
                                }
                            }
                            
                            if (actionState is KycActionState.Error) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text((actionState as KycActionState.Error).message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
