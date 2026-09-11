package com.mobile.app.presentation.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppCard
import com.mobile.app.core.ui.components.PremiumButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendNotificationScreen(
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Send Announcement", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Broadcast a message to all shop owners and agents.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)

            AppCard {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notification Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PremiumButton(
                text = "Send to All Shops",
                onClick = { 
                    // Simulate API call
                    isLoading = true
                    // In a real app: viewModel.sendNotification(title, message)
                    isLoading = false
                    onNavigateBack()
                },
                isLoading = isLoading,
                enabled = title.isNotBlank() && message.isNotBlank()
            )
        }
    }
}
