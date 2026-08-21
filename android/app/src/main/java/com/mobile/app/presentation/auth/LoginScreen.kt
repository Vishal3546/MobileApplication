package com.mobile.app.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile.app.core.ui.components.AppTextField
import com.mobile.app.core.ui.components.PasswordField
import com.mobile.app.core.ui.components.PrimaryButton

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToDashboard: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val error by viewModel.loginError.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // Observe auth state for navigation
    LaunchedEffect(authState) {
        if (authState == com.mobile.app.domain.model.AuthState.Authenticated) {
            onNavigateToDashboard()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MobileBiz",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Sign in to your account",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        AppTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username or Mobile"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Password"
        )
        
        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        PrimaryButton(
            text = "Login",
            onClick = {
                viewModel.login(username, password) { loading ->
                    isLoading = loading
                }
            },
            isLoading = isLoading,
            enabled = username.isNotBlank() && password.isNotBlank()
        )
    }
}
