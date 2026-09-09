package com.mobile.app.presentation.device.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.domain.model.device.DeviceCreate

@Composable
fun CreateDeviceScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateDeviceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is CreateDeviceUiState.Success) {
            onNavigateBack()
        }
    }

    Scaffold { padding ->
        DeviceFormContent(
            modifier = Modifier.padding(padding),
            isLoading = uiState is CreateDeviceUiState.Loading,
            errorMessage = (uiState as? CreateDeviceUiState.Error)?.message,
            onSubmit = { deviceCreate ->
                viewModel.createDevice(
                    deviceCreate.brand,
                    deviceCreate.model,
                    deviceCreate.variant,
                    deviceCreate.color,
                    deviceCreate.storage,
                    deviceCreate.ram,
                    deviceCreate.imei1,
                    deviceCreate.imei2,
                    deviceCreate.serialNumber
                )
            }
        )
    }
}

@Composable
fun DeviceFormContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    buttonText: String = "Create Device",
    onSubmit: (DeviceCreate) -> Unit
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

    val scrollState = androidx.compose.foundation.rememberScrollState()

    Column(
        modifier = modifier
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
                onSubmit(DeviceCreate(
                    brand, model, variant.takeIf { it.isNotBlank() }, color, storage, ram, imei1, imei2.takeIf { it.isNotBlank() }, serialNumber.takeIf { it.isNotBlank() }
                ))
            },
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(buttonText)
            }
        }

        if (errorMessage != null) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
