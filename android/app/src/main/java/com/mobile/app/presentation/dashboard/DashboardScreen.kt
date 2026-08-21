package com.mobile.app.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobile.app.core.ui.components.AppCard
import com.mobile.app.core.ui.components.AppTopBar

@Composable
fun DashboardScreen(
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Dashboard",
                onLogoutClick = onLogout
            )
        }
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
            item { DashboardCard("Sales", "Pending integration") }
            item { DashboardCard("Purchases", "Pending integration") }
            item { DashboardCard("Inventory", "Pending integration") }
            item { DashboardCard("Profit", "Pending integration") }
            item { DashboardCard("Pending Tasks", "Pending integration") }
        }
    }
}

@Composable
private fun DashboardCard(title: String, subtitle: String) {
    AppCard {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
