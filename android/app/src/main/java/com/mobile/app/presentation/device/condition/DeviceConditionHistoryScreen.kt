package com.mobile.app.presentation.device.condition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DeviceConditionHistoryScreen(
    deviceId: String = "placeholder_id",
    viewModel: DeviceConditionViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()
    
    LaunchedEffect(deviceId) {
        viewModel.loadHistory(deviceId)
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text("Condition History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
            
            LazyColumn {
                items(history) { condition ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Date: ${condition.recordedAt}")
                            Text("Recorded by: ${condition.recordedBy}")
                            Text("Battery Health: ${condition.batteryHealth}%")
                            Text("Display: ${condition.displayCondition}")
                            Text("Body: ${condition.bodyCondition}")
                            if (!condition.notes.isNullOrBlank()) {
                                Text("Notes: ${condition.notes}")
                            }
                        }
                    }
                }
            }
        }
    }
}
