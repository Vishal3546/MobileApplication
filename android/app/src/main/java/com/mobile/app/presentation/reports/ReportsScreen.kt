package com.mobile.app.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedIndex by remember { mutableStateOf(0) }
    val ranges = listOf("DAILY", "MONTHLY", "YEARLY")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // 1. Time Range Selector (Using TabRow for better compatibility)
            TabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp)),
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                indicator = {},
                divider = {}
            ) {
                ranges.forEachIndexed { index, range ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            viewModel.fetchReports(range)
                        },
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selectedIndex == index) MaterialTheme.colorScheme.primary 
                                else Color.Transparent
                            ),
                        text = {
                            Text(
                                text = range.lowercase().replaceFirstChar { it.uppercase() },
                                color = if (selectedIndex == index) MaterialTheme.colorScheme.onPrimary 
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 2. Sales Summary
                    uiState.sales?.let { sales ->
                        ReportSectionTitle("Sales Performance")
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MetricCard("Total Revenue", "₹${sales.totalSales}", Icons.Rounded.Payments, Color(0xFF4F46E5), Modifier.weight(1f))
                            MetricCard("Net Profit", "₹${sales.totalProfit}", Icons.Rounded.TrendingUp, Color(0xFF10B981), Modifier.weight(1f))
                        }
                        AppCard {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Orders Count", color = MaterialTheme.colorScheme.outline)
                                Text("${sales.saleCount}", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Avg. Order Value", color = MaterialTheme.colorScheme.outline)
                                Text("₹${sales.averageSaleValue}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 3. Purchase Summary
                    uiState.purchases?.let { purchases ->
                        ReportSectionTitle("Buyback Activity")
                        MetricCard("Total Outflow", "₹${purchases.totalPurchases}", Icons.Rounded.ShoppingBag, Color(0xFFF59E0B))
                        AppCard {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Devices Purchased", color = MaterialTheme.colorScheme.outline)
                                Text("${purchases.purchaseCount}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 4. Inventory Summary
                    uiState.inventory?.let { inv ->
                        ReportSectionTitle("Current Inventory")
                        MetricCard("Stock Value", "₹${inv.totalValue}", Icons.Rounded.Inventory2, Color(0xFFEC4899))
                        AppCard {
                            Text("Stock by Status", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(12.dp))
                            inv.statusBreakdown.forEach { (status, count) ->
                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(status, style = MaterialTheme.typography.bodyMedium)
                                    Text("$count", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ReportSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun MetricCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier) {
        Column {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
        }
    }
}
