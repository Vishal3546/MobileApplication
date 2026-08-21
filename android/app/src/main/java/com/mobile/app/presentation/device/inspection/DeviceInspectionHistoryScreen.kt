package com.mobile.app.presentation.device.inspection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DeviceInspectionHistoryScreen(
    deviceId: String = "placeholder_id",
    viewModel: DeviceInspectionViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()
    
    LaunchedEffect(deviceId) {
        viewModel.loadHistory(deviceId)
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Inspection History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
            
            LazyColumn {
                items(history) { inspection ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Date: ${inspection.inspectedAt}")
                            Text("Inspector: ${inspection.inspectedBy}")
                            Text("Status: ${inspection.finalStatus}")
                            if (!inspection.notes.isNullOrBlank()) {
                                Text("Notes: ${inspection.notes}")
                            }
                        }
                    }
                }
            }
        }
    }
}
