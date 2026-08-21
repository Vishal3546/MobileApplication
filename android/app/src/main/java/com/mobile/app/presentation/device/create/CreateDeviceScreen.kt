package com.mobile.app.presentation.device.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateDeviceScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateDeviceViewModel = hiltViewModel()
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var variant by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var storage by remember { mutableStateOf("") }
    var ram by remember { mutableStateOf("") }
    var imei1 by remember { mutableStateOf("") }
    var imei2 by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is CreateDeviceUiState.Success) {
            onNavigateBack()
        }
    }

    val scrollState = androidx.compose.foundation.rememberScrollState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = variant, onValueChange = { variant = it }, label = { Text("Variant (Optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = storage, onValueChange = { storage = it }, label = { Text("Storage") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ram, onValueChange = { ram = it }, label = { Text("RAM") }, modifier = Modifier.fillMaxWidth())
            
            // IMEI Input with numeric constraint (handled by keyboard type in real app)
            OutlinedTextField(
                value = imei1, 
                onValueChange = { if (it.length <= 15 && it.all { char -> char.isDigit() }) imei1 = it }, 
                label = { Text("IMEI 1") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = imei2, 
                onValueChange = { if (it.length <= 15 && it.all { char -> char.isDigit() }) imei2 = it }, 
                label = { Text("IMEI 2 (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(value = serialNumber, onValueChange = { serialNumber = it }, label = { Text("Serial Number (Optional)") }, modifier = Modifier.fillMaxWidth())
            
            Button(
                onClick = {
                    viewModel.createDevice(
                        brand, model, variant.takeIf { it.isNotBlank() }, color, storage, ram, imei1, imei2.takeIf { it.isNotBlank() }, serialNumber.takeIf { it.isNotBlank() }
                    )
                },
                modifier = Modifier.padding(top = 16.dp),
                enabled = uiState !is CreateDeviceUiState.Loading
            ) {
                Text("Create Device")
            }

            if (uiState is CreateDeviceUiState.Loading) {
                CircularProgressIndicator()
            }
            if (uiState is CreateDeviceUiState.Error) {
                Text((uiState as CreateDeviceUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
