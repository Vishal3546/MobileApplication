package com.mobile.app.presentation.inventory.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: InventoryListViewModel = hiltViewModel()
) {
    val items = viewModel.inventoryPagingFlow.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }

    val statuses = listOf(null, "AVAILABLE", "RESERVED", "IN_TRANSIT", "SOLD", "RETURNED", "DAMAGED", "BLOCKED")
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory List") },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.updateSearch(it)
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                placeholder = { Text("Search by model, brand, IMEI...") }
            )

            LazyRow(modifier = Modifier.padding(8.dp)) {
                items(statuses) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { 
                            selectedStatus = status
                            viewModel.updateStatus(status)
                        },
                        label = { Text(status ?: "ALL") },
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items.itemCount) { index ->
                    val inventory = items[index]
                    if (inventory != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            onClick = { onNavigateToDetail(inventory.id.toString()) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "${inventory.brand} ${inventory.model}", style = MaterialTheme.typography.titleMedium)
                                Text(text = "Code: ${inventory.stockCode}")
                                Text(text = "Status: ${inventory.status}")
                                Text(text = "Price: $${inventory.sellingPrice}")
                            }
                        }
                    }
                }

                items.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                        }
                        loadState.refresh is LoadState.Error -> {
                            item { Text("Error loading inventory", color = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}
