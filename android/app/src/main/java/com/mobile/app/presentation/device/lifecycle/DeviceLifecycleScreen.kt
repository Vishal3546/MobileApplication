package com.mobile.app.presentation.device.lifecycle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DeviceLifecycleScreen(
    deviceId: String = "placeholder_id",
    viewModel: DeviceLifecycleViewModel = hiltViewModel()
) {
    val events by viewModel.events.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(deviceId) {
        viewModel.loadLifecycle(deviceId)
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Device Lifecycle", style = MaterialTheme.typography.titleLarge)
            
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 16.dp))
            }
            
            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(events) { event ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Event: ${event.eventType}", style = MaterialTheme.typography.titleMedium)
                            Text("Date/Time: ${event.timestamp}")
                            Text("Performer: ${event.performerId}")
                            if (!event.branchId.isNullOrBlank()) {
                                Text("Branch: ${event.branchId}")
                            }
                        }
                    }
                }
            }
        }
    }
}
