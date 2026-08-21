package com.mobile.app.presentation.kyc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.domain.model.KycDocument
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycDocumentListScreen(
    customerId: String,
    onNavigateBack: () -> Unit,
    onNavigateToUpload: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: KycViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()

    LaunchedEffect(customerId) {
        viewModel.loadDocuments(UUID.fromString(customerId))
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("KYC Documents") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToUpload(customerId) }) {
                Icon(Icons.Default.Add, contentDescription = "Upload KYC")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = listState) {
                is KycListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is KycListState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadDocuments(UUID.fromString(customerId)) }) {
                            Text("Retry")
                        }
                    }
                }
                is KycListState.Success -> {
                    if (state.documents.isEmpty()) {
                        Text(
                            "No KYC documents uploaded.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.documents) { doc ->
                                KycDocumentCard(doc) {
                                    onNavigateToDetail(doc.id.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KycDocumentCard(doc: KycDocument, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Type: ${doc.idType.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "ID: ${doc.idNumberMasked}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(doc.verificationStatus.name) })
            }
            if (doc.verificationNotes != null) {
                Text(text = "Notes: ${doc.verificationNotes}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
