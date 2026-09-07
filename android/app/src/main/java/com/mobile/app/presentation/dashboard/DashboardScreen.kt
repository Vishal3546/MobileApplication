package com.mobile.app.presentation.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile.app.core.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToSales: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToDevices: () -> Unit = {},
    onNavigateToNetworkInventory: () -> Unit = {},
    onNavigateToSettlements: () -> Unit = {}
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
            // 1. Top Chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = true,
                        onClick = { },
                        label = { Text("My Shop") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("Network") }
                    )
                }
            }

            // 2. Search Bar
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search inventory or network...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ),
                    singleLine = true
                )
            }

            // 3. Promotional / Stats Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFE53935), Color(0xFF8E24AA))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Supercharge Your Sales",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "List your devices to the global network today.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToDevices,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFFE53935)
                            )
                        ) {
                            Text("Add Device")
                        }
                    }
                }
            }

            // 4. Shop Management Grid
            item {
                Text(
                    text = "Manage Shop",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Using Rows and Columns to build a custom grid
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ShopActionItem("Sales", Icons.Rounded.ShoppingCart, onClick = onNavigateToSales)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            ShopActionItem("Purchases", Icons.Rounded.LocalMall, onClick = onNavigateToPurchases)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            ShopActionItem("Inventory", Icons.Rounded.Inventory, onClick = onNavigateToInventory)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            ShopActionItem("Devices", Icons.Rounded.Smartphone, onClick = onNavigateToDevices)
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ShopActionItem("Settlements", Icons.Rounded.Payments, onClick = onNavigateToSettlements)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            ShopActionItem("Network", Icons.Rounded.Language, onClick = onNavigateToNetworkInventory)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {} // Empty placeholders for alignment
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.weight(1f)) {} // Empty placeholders for alignment
                    }
                }
            }
        }
    }
}

@Composable
private fun ShopActionItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom visual feedback via scale
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun DashboardBottomNav() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "Inventory", "Network", "Profile")
    val icons = listOf(Icons.Rounded.Home, Icons.Rounded.Inventory, Icons.Rounded.Language, Icons.Rounded.Person)
    
    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}
