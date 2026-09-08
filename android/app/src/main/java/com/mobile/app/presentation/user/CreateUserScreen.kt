package com.mobile.app.presentation.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppTopBar
import com.mobile.app.data.remote.dto.user.CreateUserRequestDto
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateUserViewModel = hiltViewModel()
) {
    val state by viewModel.createUserState.collectAsState()
    val roles by viewModel.roles.collectAsState()
    val shops by viewModel.shops.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    
    var selectedRoleId by remember { mutableStateOf<UUID?>(null) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    var selectedShopId by remember { mutableStateOf<UUID?>(null) }
    var shopDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is CreateUserState.Success) {
            viewModel.resetCreateState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Create New User",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state is CreateUserState.Error) {
                Text(
                    text = (state as CreateUserState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = roleDropdownExpanded,
                onExpandedChange = { roleDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = roles.find { it.id == selectedRoleId }?.name ?: "Select Role",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Role") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = roleDropdownExpanded,
                    onDismissRequest = { roleDropdownExpanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role.name) },
                            onClick = {
                                selectedRoleId = role.id
                                roleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            val selectedRole = roles.find { it.id == selectedRoleId }
            if (selectedRole?.name == "ADMIN" || selectedRole?.name == "SUPER_ADMIN" || selectedRole?.name == "SHOP_OWNER") {
                ExposedDropdownMenuBox(
                    expanded = shopDropdownExpanded,
                    onExpandedChange = { shopDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = shops.find { it.id == selectedShopId }?.name ?: "Select Shop (Optional)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign to Shop") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = shopDropdownExpanded,
                        onDismissRequest = { shopDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedShopId = null
                                shopDropdownExpanded = false
                            }
                        )
                        shops.forEach { shop ->
                            DropdownMenuItem(
                                text = { Text(shop.name) },
                                onClick = {
                                    selectedShopId = shop.id
                                    shopDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && phone.isNotBlank()) {
                        viewModel.createUser(
                            CreateUserRequestDto(
                                username = username,
                                password = password,
                                firstName = firstName,
                                lastName = lastName,
                                email = email,
                                phone = phone,
                                roleIds = selectedRoleId?.let { listOf(it) },
                                shopId = selectedShopId
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = username.isNotBlank() && password.isNotBlank() && state !is CreateUserState.Loading
            ) {
                if (state is CreateUserState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create User")
                }
            }
        }
    }
}
