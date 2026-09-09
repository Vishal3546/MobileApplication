package com.mobile.app.presentation.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobile.app.core.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    isSuperAdmin: Boolean = false,
    onLogout: () -> Unit,
    onNavigateToSales: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {},
    onNavigateToNetworkInventory: () -> Unit = {},
    onNavigateToSettlements: () -> Unit = {},
    onNavigateToShops: () -> Unit = {},
    onNavigateToCreateUser: () -> Unit = {},
    onNavigateToBuyback: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard",
                onLogoutClick = onLogout
            )
        },
        bottomBar = {
            DashboardBottomNav()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Welcome & Summary Section
            item {
                Column {
                    Text(
                        text = "Hello, ${if (isSuperAdmin) "Admin" else "Partner"}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Here's what's happening today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }

            // 2. High-Impact KPI Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KPICard(
                        title = "Total Sales",
                        value = "₹45,230",
                        icon = Icons.Rounded.TrendingUp,
                        color = Color(0xFF4F46E5),
                        modifier = Modifier.weight(1f)
                    )
                    KPICard(
                        title = "Active Stock",
                        value = "128",
                        icon = Icons.Rounded.Inventory,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 3. Quick Action Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
                            )
                        )
                        .clickable { onNavigateToBuyback() }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ready to buy?",
                                style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Start a new device evaluation now",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // 4. Core Operations Grid
            item {
                Text(
                    text = "Core Operations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OperationCard("Sales", Icons.Rounded.ReceiptLong, Color(0xFFF59E0B), onNavigateToSales, Modifier.weight(1f))
                        OperationCard("Inventory", Icons.Rounded.Storage, Color(0xFFEC4899), onNavigateToInventory, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OperationCard("Purchases", Icons.Rounded.ShoppingBag, Color(0xFF3B82F6), onNavigateToPurchases, Modifier.weight(1f))
                        OperationCard("Devices", Icons.Rounded.PhoneAndroid, Color(0xFF8B5CF6), onNavigateToDevices, Modifier.weight(1f))
                    }
                }
            }

            // 5. Admin Utilities
            if (isSuperAdmin) {
                item {
                    Text(
                        text = "Administration",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OperationCard("Manage Shops", Icons.Rounded.Storefront, Color(0xFF14B8A6), onNavigateToShops, Modifier.weight(1f))
                        OperationCard("Add User", Icons.Rounded.PersonAdd, Color(0xFFF43F5E), onNavigateToCreateUser, Modifier.weight(1f))
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun KPICard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.1f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.padding(6.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun OperationCard(title: String, icon: ImageVector, iconColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun DashboardBottomNav() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Inventory", "Network", "Profile")
    val icons = listOf(Icons.Rounded.Home, Icons.Rounded.Inventory2, Icons.Rounded.Public, Icons.Rounded.Person)
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.outline,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}
