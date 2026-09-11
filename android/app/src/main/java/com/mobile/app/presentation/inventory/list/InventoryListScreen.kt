package com.mobile.app.presentation.inventory.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mobile.app.core.ui.components.AppCard
import com.mobile.app.presentation.inventory.InventoryListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: InventoryListViewModel = hiltViewModel()
) {
    val items = viewModel.inventoryPagingFlow.collectAsLazyPagingItems()
    var searchQuery by remember { mutableStateOf("") }

    val statuses = listOf(null, "AVAILABLE", "RESERVED", "IN_TRANSIT", "SOLD", "DAMAGED")
    var selectedStatus by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Inventory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.updateSearch(it)
                },
                placeholder = { Text("Search brand, model, or IMEI") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            // Status Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statuses) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { 
                            selectedStatus = status
                            viewModel.updateStatus(status)
                        },
                        label = { Text(status?.lowercase()?.capitalize() ?: "All Stock") },
                        leadingIcon = {
                            if (selectedStatus == status) {
                                Icon(Icons.Rounded.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    )
                }
            }

            if (items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No stock found in this category.", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items.itemCount) { index ->
                    val inventory = items[index]
                    if (inventory != null) {
                        InventoryListItem(
                            inventory = inventory,
                            onClick = { onNavigateToDetail(inventory.id.toString()) }
                        )
                    }
                }

                items.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                        }
                        loadState.append is LoadState.Loading -> {
                            item { Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                        }
                        loadState.refresh is LoadState.Error -> {
                            item { 
                                Text("Error loading inventory. Please try again.", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
