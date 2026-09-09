package com.mobile.app.presentation.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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

    // Auto-fill logic when shop is selected
    LaunchedEffect(selectedShopId) {
        if (selectedShopId != null) {
            val selectedShop = shops.find { it.id == selectedShopId }
            selectedShop?.let {
                // Heuristic: If shop has a name like "John Doe Shop", we can try to split it, 
                // but usually shops have emails and contact names. 
                // For now, we'll use the shop's email if available.
                email = it.email ?: ""
                phone = it.phone ?: ""
                
                // If the shop name contains a space, we can guess first/last name
                val nameParts = it.name.split(" ")
                if (nameParts.size >= 2) {
                    firstName = nameParts[0]
                    lastName = nameParts.drop(1).joinToString(" ")
                } else {
                    firstName = it.name
                    lastName = ""
                }
            }
        }
    }

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

            Text("Selection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            // Shop Selection Dropdown (Primary Option as requested)
            ExposedDropdownMenuBox(
                expanded = shopDropdownExpanded,
                onExpandedChange = { shopDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = shops.find { it.id == selectedShopId }?.name ?: "Select Shop",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Shop") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = shopDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                ExposedDropdownMenu(
                    expanded = shopDropdownExpanded,
                    onDismissRequest = { shopDropdownExpanded = false }
                ) {
                    shops.forEach { shop ->
                        DropdownMenuItem(
                            text = { Text(shop.name) },
                            onClick = {
                                selectedShopId = shop.id
                                shopDropdownExpanded = false
                                // Automatically set role to SHOP_OWNER if a shop is selected
                                val shopOwnerRole = roles.find { it.name == "SHOP_OWNER" }
                                if (shopOwnerRole != null) {
                                    selectedRoleId = shopOwnerRole.id
                                }
                            }
                        )
                    }
                }
            }

            // Role Selection
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

            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("User Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = username.isNotBlank() && password.isNotBlank() && state !is CreateUserState.Loading
            ) {
                if (state is CreateUserState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Create User Account", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
