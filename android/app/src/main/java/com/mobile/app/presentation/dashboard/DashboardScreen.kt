package com.mobile.app.presentation.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mobile.app.core.ui.components.AppTopBar

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onNavigateToSales: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToCustomers: () -> Unit = {},
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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { DashboardCard("Sales", "Manage sales", onClick = onNavigateToSales) }
            item { DashboardCard("Purchases", "Manage purchases", onClick = onNavigateToPurchases) }
            item { DashboardCard("Inventory", "Manage stock", onClick = onNavigateToInventory) }
            item { DashboardCard("Customers", "Customer profiles", onClick = onNavigateToCustomers) }
            item { DashboardCard("Devices", "Device lifecycle", onClick = onNavigateToDevices) }
            item { DashboardCard("Network", "Network inventory", onClick = onNavigateToNetworkInventory) }
            item { DashboardCard("Settlements", "Manage payments", onClick = onNavigateToSettlements) }
        }
    }
}

@Composable
private fun DashboardCard(
    title: String, 
    subtitle: String, 
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.foundation.LocalIndication.current,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title, 
                style = MaterialTheme.typography.titleMedium, 
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle, 
                style = MaterialTheme.typography.bodySmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
