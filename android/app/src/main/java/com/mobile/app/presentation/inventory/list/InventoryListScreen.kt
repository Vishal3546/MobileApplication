package com.mobile.app.presentation.inventory.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.presentation.inventory.InventoryListItem
import java.math.BigDecimal

enum class InventoryViewMode {
    FLAT, GROUPED
}

data class BrandGroup(
    val brand: String,
    val count: Int,
    val totalValue: BigDecimal,
    val items: List<Inventory>
)

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
    var viewMode by remember { mutableStateOf(InventoryViewMode.FLAT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Inventory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Toggle Flat / Grouped View
                    IconButton(onClick = {
                        viewMode = if (viewMode == InventoryViewMode.FLAT) InventoryViewMode.GROUPED else InventoryViewMode.FLAT
                    }) {
                        Icon(
                            imageVector = if (viewMode == InventoryViewMode.FLAT) Icons.Rounded.GridView else Icons.AutoMirrored.Rounded.List,
                            contentDescription = "Toggle View Mode"
                        )
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
                        label = { Text(status?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "All Stock") },
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
            } else {
                when (viewMode) {
                    InventoryViewMode.FLAT -> {
                        // Flat View Mode
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
                        }
                    }
                    InventoryViewMode.GROUPED -> {
                        // Grouped View Mode (Brand-wise 1-level Accordion)
                        val groupedItems = remember(items.itemSnapshotList) {
                            items.itemSnapshotList.items
                                .groupBy { it.brand }
                                .map { (brand, list) ->
                                    BrandGroup(
                                        brand = brand,
                                        count = list.size,
                                        totalValue = list.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.sellingPrice) },
                                        items = list
                                    )
                                }
                                .sortedByDescending { it.count }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(groupedItems) { group ->
                                BrandGroupAccordion(
                                    group = group,
                                    onItemClick = { inventory -> onNavigateToDetail(inventory.id.toString()) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BrandGroupAccordion(
    group: BrandGroup,
    onItemClick: (Inventory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 90f else 0f, label = "arrowRotation")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${group.brand} (${group.count})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Total Stock Value: ₹${group.totalValue}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = "Expand",
                    modifier = Modifier.rotate(rotationState)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    group.items.forEach { inventory ->
                        InventoryListItem(
                            inventory = inventory,
                            onClick = { onItemClick(inventory) }
                        )
                    }
                }
            }
        }
    }
}
