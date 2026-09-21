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
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mobile.app.core.utils.CurrencyFormatter
import com.mobile.app.domain.model.inventory.BrandSummary
import com.mobile.app.domain.model.inventory.Inventory
import com.mobile.app.presentation.inventory.InventoryListItem
import java.math.BigDecimal

enum class InventoryViewMode {
    FLAT, GROUPED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: InventoryListViewModel = hiltViewModel()
) {
    val items = viewModel.inventoryPagingFlow.collectAsLazyPagingItems()
    val viewMode by viewModel.viewMode.collectAsState()
    val groupedState by viewModel.groupedState.collectAsState()
    val activeFilters by viewModel.activeFilters.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    val statuses = listOf(null, "AVAILABLE", "RESERVED", "IN_TRANSIT", "SOLD", "DAMAGED")
    var selectedStatus by remember { mutableStateOf<String?>(null) }

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
                    IconButton(onClick = { viewModel.toggleViewMode() }) {
                        Icon(
                            imageVector = if (viewMode == InventoryViewMode.FLAT) {
                                Icons.Rounded.GridView
                            } else {
                                Icons.AutoMirrored.Rounded.List
                            },
                            contentDescription = if (viewMode == InventoryViewMode.FLAT) {
                                "Switch to brand-wise grouped view"
                            } else {
                                "Switch to flat list view"
                            }
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
            // Search Bar (input is debounced inside the ViewModel)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.updateSearch(it)
                },
                placeholder = { Text("Search brand, model, or IMEI") },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statuses) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = {
                            selectedStatus = status
                            viewModel.updateStatus(status)
                        },
                        label = { Text(prettyStatus(status)) },
                        leadingIcon = {
                            if (selectedStatus == status) {
                                Icon(
                                    Icons.Rounded.FilterList,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    )
                }
            }

            when (viewMode) {
                InventoryViewMode.FLAT -> FlatInventoryList(
                    items = items,
                    onNavigateToDetail = onNavigateToDetail
                )

                InventoryViewMode.GROUPED -> GroupedInventoryList(
                    groupedState = groupedState,
                    // Pass the committed (debounced) search so brand expansions
                    // don't fire a request on every keystroke.
                    activeSearch = activeFilters.search ?: "",
                    status = selectedStatus,
                    onRetry = { viewModel.loadGroupedSummary() },
                    onNavigateToDetail = onNavigateToDetail
                )
            }
        }
    }
}

private fun prettyStatus(status: String?): String {
    return status?.lowercase()
        ?.split('_')
        ?.joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
        ?: "All Stock"
}

/* ------------------------------- FLAT VIEW -------------------------------- */

@Composable
private fun FlatInventoryList(
    items: LazyPagingItems<Inventory>,
    onNavigateToDetail: (String) -> Unit
) {
    if (items.loadState.refresh is LoadState.NotLoading && items.itemCount == 0) {
        EmptyState(message = "No stock found in this category.")
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (items.loadState.refresh is LoadState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
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
        }
    }
}

/* ------------------------------ GROUPED VIEW ------------------------------ */

@Composable
private fun GroupedInventoryList(
    groupedState: GroupedUiState,
    activeSearch: String,
    status: String?,
    onRetry: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    when {
        groupedState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        groupedState.error != null -> {
            ErrorState(message = groupedState.error, onRetry = onRetry)
        }

        groupedState.groups.isEmpty() -> {
            EmptyState(message = "No stock found in this category.")
        }

        else -> {
            val totalDevices = groupedState.groups.sumOf { it.count }
            val totalValue = groupedState.groups.fold(BigDecimal.ZERO) { acc, group ->
                acc.add(group.totalValue)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "grouped_summary_header") {
                    GroupedSummaryHeader(
                        brandCount = groupedState.groups.size,
                        deviceCount = totalDevices,
                        totalValue = totalValue
                    )
                }

                items(groupedState.groups, key = { it.brand }) { group ->
                    BrandGroupAccordion(
                        summary = group,
                        status = status,
                        searchQuery = activeSearch,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupedSummaryHeader(
    brandCount: Int,
    deviceCount: Int,
    totalValue: BigDecimal
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$deviceCount ${if (deviceCount == 1) "device" else "devices"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "across ${if (brandCount == 1) "1 brand" else "$brandCount brands"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatRupees(totalValue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "total stock value",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun BrandGroupAccordion(
    summary: BrandSummary,
    status: String?,
    searchQuery: String,
    onNavigateToDetail: (String) -> Unit
) {
    var expanded by remember(summary.brand) { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        label = "chevronRotation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = summary.brand,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${summary.count} ${if (summary.count == 1) "unit" else "units"} • " +
                                CurrencyFormatter.formatRupees(summary.totalValue),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = if (expanded) "Collapse ${summary.brand}" else "Expand ${summary.brand}",
                    modifier = Modifier.rotate(chevronRotation)
                )
            }

            AnimatedVisibility(visible = expanded) {
                BrandDevicesSection(
                    brand = summary.brand,
                    status = status,
                    searchQuery = searchQuery,
                    onNavigateToDetail = onNavigateToDetail
                )
            }
        }
    }
}

/**
 * The device list shown inside an expanded brand accordion. Devices are fetched
 * lazily — only when the brand is expanded — and cached per brand.
 */
@Composable
private fun BrandDevicesSection(
    brand: String,
    status: String?,
    searchQuery: String,
    onNavigateToDetail: (String) -> Unit
) {
    val viewModel: BrandDeviceListViewModel = hiltViewModel(key = "brand_devices_$brand")

    LaunchedEffect(brand, status, searchQuery) {
        viewModel.loadDevices(brand = brand, status = status, search = searchQuery)
    }

    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 3.dp
                )
            }
        }

        uiState.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.error ?: "Failed to load devices",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                TextButton(onClick = {
                    viewModel.loadDevices(brand = brand, status = status, search = searchQuery)
                }) {
                    Text("Retry")
                }
            }
        }

        uiState.devices.isEmpty() -> {
            Text(
                text = "No devices found.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        else -> {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.devices.forEach { inventory ->
                    InventoryListItem(
                        inventory = inventory,
                        onClick = { onNavigateToDetail(inventory.id.toString()) }
                    )
                }

                if (uiState.totalCount > uiState.devices.size) {
                    Text(
                        text = "Showing ${uiState.devices.size} of ${uiState.totalCount} — " +
                                "switch to flat view to see all",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/* --------------------------- SHARED EMPTY / ERROR -------------------------- */

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Rounded.Refresh,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
