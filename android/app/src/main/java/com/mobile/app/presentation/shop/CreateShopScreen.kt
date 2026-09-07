package com.mobile.app.presentation.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppTopBar
import com.mobile.app.data.remote.dto.shop.CreateShopRequestDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShopScreen(
    onNavigateBack: () -> Unit,
    viewModel: ShopViewModel = hiltViewModel()
) {
    val state by viewModel.createShopState.collectAsState()

    var name by remember { mutableStateOf("") }
    var legalName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var stateField by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is CreateShopState.Success) {
            viewModel.resetCreateState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Register New Shop",
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
            if (state is CreateShopState.Error) {
                Text(
                    text = (state as CreateShopState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Shop Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = legalName,
                onValueChange = { legalName = it },
                label = { Text("Legal Entity Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Contact Phone") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Street Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = stateField,
                    onValueChange = { stateField = it },
                    label = { Text("State") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.createShop(
                            CreateShopRequestDto(
                                name = name,
                                legalName = legalName.takeIf { it.isNotBlank() },
                                phone = phone.takeIf { it.isNotBlank() },
                                email = email.takeIf { it.isNotBlank() },
                                address = address.takeIf { it.isNotBlank() },
                                city = city.takeIf { it.isNotBlank() },
                                state = stateField.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && state !is CreateShopState.Loading
            ) {
                if (state is CreateShopState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Register Shop")
                }
            }
        }
    }
}
