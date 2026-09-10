package com.mobile.app.presentation.device.create

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppTopBar
import com.mobile.app.core.ui.components.SearchableSelect2Dropdown
import com.mobile.app.domain.model.device.DeviceCatalog
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

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Add New Device",
                onBackClick = onNavigateBack
            )
        }
    ) { padding ->
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
    buttonText: String = "Save Device Details",
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

    val scrollState = rememberScrollState()

    // Available models dynamically filtered by selected brand
    val availableModels = remember(brand) {
        DeviceCatalog.modelsByBrand[brand] ?: listOf(
            "$brand Pro Max", "$brand Pro", "$brand Ultra", "$brand Lite", "$brand Standard"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Device Specifications",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // 1. Brand Selection (Searchable Select2 Dropdown)
        SearchableSelect2Dropdown(
            label = "Brand *",
            selectedValue = brand,
            options = DeviceCatalog.brands,
            onOptionSelected = { selected ->
                if (brand != selected) {
                    brand = selected
                    model = ""
                    variant = ""
                    color = ""
                    storage = ""
                    ram = ""
                }
            },
            placeholder = "Select Brand"
        )

        // 2. Model Selection (Cascading from Brand)
        SearchableSelect2Dropdown(
            label = "Model *",
            selectedValue = model,
            options = availableModels,
            onOptionSelected = { selected ->
                if (model != selected) {
                    model = selected
                    variant = ""
                }
            },
            placeholder = if (brand.isBlank()) "Select Brand First" else "Select Model",
            enabled = brand.isNotBlank()
        )

        // 3. Variant Selection
        SearchableSelect2Dropdown(
            label = "Variant (Optional)",
            selectedValue = variant,
            options = DeviceCatalog.variants,
            onOptionSelected = { variant = it },
            placeholder = "Select Variant",
            enabled = model.isNotBlank()
        )

        // 4. Color Selection
        SearchableSelect2Dropdown(
            label = "Color *",
            selectedValue = color,
            options = DeviceCatalog.colors,
            onOptionSelected = { color = it },
            placeholder = "Select Color",
            enabled = model.isNotBlank()
        )

        // 5. Storage Selection
        SearchableSelect2Dropdown(
            label = "Storage *",
            selectedValue = storage,
            options = DeviceCatalog.storageOptions,
            onOptionSelected = { storage = it },
            placeholder = "Select Storage",
            enabled = model.isNotBlank()
        )

        // 6. RAM Selection
        SearchableSelect2Dropdown(
            label = "RAM *",
            selectedValue = ram,
            options = DeviceCatalog.ramOptions,
            onOptionSelected = { ram = it },
            placeholder = "Select RAM",
            enabled = model.isNotBlank()
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Device Identification (IMEI & Serial)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = imei1,
            onValueChange = { if (it.length <= 15 && it.all { char -> char.isDigit() }) imei1 = it },
            label = { Text("IMEI 1 (15 Digits) *") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = imei2,
            onValueChange = { if (it.length <= 15 && it.all { char -> char.isDigit() }) imei2 = it },
            label = { Text("IMEI 2 (15 Digits - Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = serialNumber,
            onValueChange = { serialNumber = it },
            label = { Text("Serial Number (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onSubmit(
                    DeviceCreate(
                        brand = brand,
                        model = model,
                        variant = variant.takeIf { it.isNotBlank() },
                        color = color,
                        storage = storage,
                        ram = ram,
                        imei1 = imei1,
                        imei2 = imei2.takeIf { it.isNotBlank() },
                        serialNumber = serialNumber.takeIf { it.isNotBlank() }
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading && brand.isNotBlank() && model.isNotBlank() && color.isNotBlank() && storage.isNotBlank() && ram.isNotBlank() && imei1.length == 15
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
