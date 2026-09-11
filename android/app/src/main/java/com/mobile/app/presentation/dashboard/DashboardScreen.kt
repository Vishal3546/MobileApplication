package com.mobile.app.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    onNavigateToBuyback: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToSell: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {}
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        bottomBar = { DashboardBottomNav() },
        containerColor = Color(0xFFF8FAFC) // Very light slate
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Premium Header (Glassmorphic feel)
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome back,",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                            Text(
                                text = if (isSuperAdmin) "Admin" else "Store Manager",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFF1E293B)
                            )
                        }
                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                        ) {
                            Icon(Icons.Rounded.Logout, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }

            // 2. Main KPI - High Performance Look
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(600)) + expandVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column(modifier = Modifier.align(Alignment.CenterStart)) {
                            Text("Total Portfolio Value", color = Color.White.copy(alpha = 0.7f))
                            Text("₹12,45,230", style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontWeight = FontWeight.Black))
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    "+12.5% this month",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                        Icon(
                            Icons.Rounded.Assessment,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).align(Alignment.CenterEnd).graphicsLayer { alpha = 0.1f },
                            tint = Color.White
                        )
                    }
                }
            }

            // 3. Dual Action Banners
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionBanner(
                        title = "Buy",
                        subtitle = "Device Buyback",
                        icon = Icons.Rounded.AddShoppingCart,
                        color = Color(0xFF10B981),
                        onClick = onNavigateToBuyback,
                        modifier = Modifier.weight(1f)
                    )
                    ActionBanner(
                        title = "Sell",
                        subtitle = "Direct Sale",
                        icon = Icons.Rounded.Sell,
                        color = Color(0xFF3B82F6),
                        onClick = onNavigateToSell,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 4. Core Grid
            item {
                Text(
                    text = "Operational Suite",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SquareIconCard("Inventory", Icons.Rounded.Inventory2, Color(0xFFF59E0B), onNavigateToInventory, Modifier.weight(1f))
                        SquareIconCard("Analytics", Icons.Rounded.BarChart, Color(0xFFEC4899), onNavigateToReports, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SquareIconCard("Purchases", Icons.Rounded.LibraryAdd, Color(0xFF6366F1), onNavigateToPurchases, Modifier.weight(1f))
                        SquareIconCard("Catalog", Icons.Rounded.Devices, Color(0xFF8B5CF6), onNavigateToDevices, Modifier.weight(1f))
                    }
                }
            }

            // 5. SuperAdmin Special
            if (isSuperAdmin) {
                item {
                    Text(
                        text = "System Administration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SquareIconCard("Manage Shops", Icons.Rounded.Storefront, Color(0xFF14B8A6), onNavigateToShops, Modifier.weight(1f))
                        SquareIconCard("Send Alert", Icons.Rounded.NotificationsActive, Color(0xFF8B5CF6), onNavigateToNotifications, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    SquareIconCard("Staffing", Icons.Rounded.PersonAddAlt1, Color(0xFFF43F5E), onNavigateToCreateUser, Modifier.fillMaxWidth(0.5f))
                }
            }
        }
    }
}

@Composable
fun ActionBanner(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(110.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = color.copy(alpha = 0.3f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun SquareIconCard(title: String, icon: ImageVector, iconColor: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .aspectRatio(1.2f)
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
        }
    }
}

@Composable
fun DashboardBottomNav() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Inventory", "Network", "Settings")
    val icons = listOf(Icons.Rounded.Home, Icons.Rounded.Dashboard, Icons.Rounded.Language, Icons.Rounded.Settings)
    
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, style = MaterialTheme.typography.labelSmall) },
                selected = selectedItem == index,
                onClick = { selectedItem = index },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4F46E5),
                    unselectedIconColor = Color(0xFF94A3B8),
                    indicatorColor = Color(0xFFEEF2FF)
                )
            )
        }
    }
}
