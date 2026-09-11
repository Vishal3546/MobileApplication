package com.mobile.app.presentation.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile.app.core.ui.components.AppCard
import com.mobile.app.domain.model.inventory.Inventory

@Composable
fun InventoryListItem(
    inventory: Inventory,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${inventory.brand} ${inventory.model}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "IMEI: ${inventory.imei}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₹${inventory.sellingPrice}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Status Badge
            val statusColor = when (inventory.status) {
                "AVAILABLE" -> Color(0xFF10B981)
                "RESERVED" -> Color(0xFFF59E0B)
                "SOLD" -> Color(0xFF3B82F6)
                else -> Color(0xFF64748B)
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.1f),
            ) {
                Text(
                    text = inventory.status,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }
        }
    }
}
