package com.mobile.app.presentation.device.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mobile.app.domain.model.device.Device
import com.mobile.app.core.security.PermissionManager // Assume exists

@Composable
fun DeviceListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: DeviceListViewModel = hiltViewModel()
) {
    val devices = viewModel.devices.collectAsLazyPagingItems()
    val canCreate = true
    val canViewFullImei = true

    Scaffold(
        floatingActionButton = {
            if (canCreate) {
                FloatingActionButton(onClick = onNavigateToCreate) {
                    Text("+")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search & Filter Bar
            OutlinedTextField(
                value = viewModel.searchQuery.collectAsState().value,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Search Devices") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            
            // Simple Filter UI placeholder
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { /* Open Status Filter */ }) { Text("Status") }
                Button(onClick = { /* Open Brand Filter */ }) { Text("Brand") }
                Button(onClick = { /* Open Model Filter */ }) { Text("Model") }
            }

            if (devices.loadState.refresh is LoadState.NotLoading && devices.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("No devices found.", style = MaterialTheme.typography.bodyLarge)
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(devices.itemCount) { index ->
                    val device = devices[index]
                    if (device != null) {
                        DeviceListItem(
                            device = device,
                            canViewFullImei = canViewFullImei,
                            onClick = { onNavigateToDetail(device.id) }
                        )
                    }
                }

                devices.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val error = devices.loadState.refresh as LoadState.Error
                            item { 
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                                    Text("Error loading devices: ${error.error.message}")
                                    Button(onClick = { devices.retry() }) { Text("Retry") }
                                }
                            }
                        }
                        loadState.append is LoadState.Error -> {
                            item {
                                Button(onClick = { devices.retry() }, modifier = Modifier.padding(16.dp)) {
                                    Text("Retry Loading More")
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
fun DeviceListItem(
    device: Device,
    canViewFullImei: Boolean,
    onClick: () -> Unit
) {
    val displayImei = if (canViewFullImei) {
        device.imei1
    } else {
        device.imei1.takeLast(4).padStart(device.imei1.length, '*')
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${device.brand} ${device.model} ${device.variant ?: ""} - ${device.color}", style = MaterialTheme.typography.titleMedium)
            Text("${device.ram} RAM / ${device.storage} Storage", style = MaterialTheme.typography.bodyMedium)
            Text("IMEI: $displayImei", style = MaterialTheme.typography.bodyMedium)
            Text("Status: ${device.status}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
