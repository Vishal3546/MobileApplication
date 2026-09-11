package com.mobile.app.presentation.inventory.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppCard
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryDetailScreen(
    inventoryId: String,
    onNavigateBack: () -> Unit,
    viewModel: InventoryDetailViewModel = hiltViewModel()
) {
    val inventory by viewModel.inventory.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(inventoryId) {
        viewModel.loadInventory(UUID.fromString(inventoryId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stock Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            inventory?.let { inv ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Device Header Card
                    AppCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Inventory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = "${inv.brand} ${inv.model}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text(text = "Stock Code: ${inv.stockCode}", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }

                    // 2. Info Grid
                    AppCard {
                        Text("Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow("IMEI Number", inv.imei)
                        InfoRow("Purchase Price", "₹${inv.purchasePrice}")
                        InfoRow("Current Selling Price", "₹${inv.sellingPrice}")
                        InfoRow("Current Status", inv.status)
                    }

                    // 3. Action Buttons
                    Text("Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (inv.status == "AVAILABLE") {
                            Button(
                                onClick = { viewModel.reserveInventory(inv.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Reserve")
                            }
                        }

                        if (inv.status == "RESERVED") {
                            Button(
                                onClick = { viewModel.releaseInventory(inv.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Release")
                            }
                        }
                        
                        OutlinedButton(
                            onClick = { /* TODO: Implement price update dialog */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Update Price")
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.changeStatus(inv.id, "DAMAGED", "Found hardware issue") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Mark as Damaged")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 4. History Preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Icon(Icons.Rounded.History, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                    }
                    
                    Text("Check history for full audit trail.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.outline, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
    }
}
