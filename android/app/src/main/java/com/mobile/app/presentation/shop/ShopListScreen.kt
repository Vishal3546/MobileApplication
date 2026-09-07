package com.mobile.app.presentation.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppTopBar
import com.mobile.app.domain.model.shop.Shop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: ShopViewModel = hiltViewModel()
) {
    val state by viewModel.shopListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchShops()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Manage Shops (Admin)",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Shop")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (state) {
                is ShopListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ShopListState.Error -> {
                    Text(
                        text = (state as ShopListState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ShopListState.Success -> {
                    val shops = (state as ShopListState.Success).shops
                    if (shops.isEmpty()) {
                        Text(
                            text = "No shops found in the network.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(shops) { shop ->
                                ShopListItem(shop = shop)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopListItem(shop: Shop) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Future: Navigate to Shop Details */ },
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Store,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = shop.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                if (!shop.city.isNullOrBlank() || !shop.state.isNullOrBlank()) {
                    Text(
                        text = "${shop.city ?: ""}, ${shop.state ?: ""}".trim(',', ' '),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = if (shop.active) "Active" else "Inactive",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (shop.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
