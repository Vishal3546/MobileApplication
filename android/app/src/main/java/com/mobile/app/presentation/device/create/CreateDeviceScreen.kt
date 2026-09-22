package com.mobile.app.presentation.device.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.core.ui.components.AppTopBar
import com.mobile.app.core.ui.components.BarcodeScannerDialog
import com.mobile.app.core.ui.components.PremiumButton
import com.mobile.app.core.ui.components.VisualGridItem
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.DeviceCatalog
import com.mobile.app.domain.model.device.DeviceCreate
import com.mobile.app.domain.model.device.ModelInfo

@Composable
fun CreateDeviceScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateDeviceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val models by viewModel.models.collectAsState()
    val fetchedDevice by viewModel.fetchedDevice.collectAsState()
    val isFetchingImei by viewModel.isFetchingImei.collectAsState()
    val isFetchingModels by viewModel.isFetchingModels.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is CreateDeviceUiState.Success) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Add New Device",
                onBackClick = onNavigateBack,
            )
        },
    ) { padding ->
        DeviceFormContent(
            modifier = Modifier.padding(padding),
            isLoading = uiState is CreateDeviceUiState.Loading,
            isFetchingImei = isFetchingImei,
            isFetchingModels = isFetchingModels,
            fetchedDevice = fetchedDevice,
            availableModels = models,
            onBrandSelected = { brand ->
                viewModel.loadModelsForBrand(brand)
            },
            onImeiEntered = { imei ->
                viewModel.fetchDeviceByImei(imei)
            },
            errorMessage = (uiState as? CreateDeviceUiState.Error)?.message,
        ) { deviceCreate ->
            viewModel.createDevice(
                deviceCreate.brand,
                deviceCreate.model,
                deviceCreate.variant,
                deviceCreate.color,
                deviceCreate.storage,
                deviceCreate.ram,
                deviceCreate.imei1,
                deviceCreate.imei2,
                deviceCreate.serialNumber,
            )
        }
    }
}

@Composable
fun DeviceFormContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isFetchingImei: Boolean = false,
    isFetchingModels: Boolean = false,
    fetchedDevice: Device? = null,
    availableModels: List<ModelInfo> = emptyList(),
    onBrandSelected: (String) -> Unit = {},
    onImeiEntered: (String) -> Unit = {},
    errorMessage: String? = null,
    buttonText: String = "Save Device",
    onSubmit: (DeviceCreate) -> Unit,
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

    var showScanner by remember { mutableStateOf(value = false) }

    // State for visual selection steps
    var currentSubStep by remember { mutableStateOf(0) } // 0: Brand, 1: Model, 2: Specs

    LaunchedEffect(fetchedDevice) {
        fetchedDevice?.let {
            // Only apply auto-detected info when it is a REAL match (TAC hit
            // or a device that already exists in the system). A "Generic"
            // marker means the IMEI could not be identified — in that case
            // keep whatever brand/model the user selected.
            val isRealMatch = it.brand.isNotBlank() &&
                    !it.brand.equals("Generic", ignoreCase = true) &&
                    it.model.isNotBlank() &&
                    !it.model.equals("Smartphone", ignoreCase = true)

            if (isRealMatch) {
                brand = it.brand
                model = it.model
                variant = it.variant ?: ""
                if (it.color.isNotBlank()) color = it.color
                storage = it.storage
                ram = it.ram
            }
            currentSubStep = 2 // Skip to specs if fetched
        }
    }

    if (showScanner) {
        BarcodeScannerDialog(
            onBarcodeDetected = { scannedImei ->
                if (scannedImei.length >= 14) {
                    imei1 = scannedImei.take(15)
                    onImeiEntered(imei1)
                    showScanner = false
                }
            },
        ) { showScanner = false }
    }

    Column(modifier = modifier.fillMaxSize()) {
        when (currentSubStep) {
            0 -> { // Brand Selection
                Text(
                    "Select Brand",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(DeviceCatalog.brands) { brandInfo ->
                        VisualGridItem(
                            title = brandInfo.name,
                            imageUrl = brandInfo.imageUrl,
                            isSelected = brand == brandInfo.name,
                            onClick = {
                                brand = brandInfo.name
                                onBrandSelected(brand)
                                currentSubStep = 1
                            },
                        )
                    }
                }
            }
            1 -> { // Model Selection
                var showManualModel by remember { mutableStateOf(false) }
                var manualModel by remember { mutableStateOf("") }

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Select $brand Model",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    if (isFetchingModels) {
                        Spacer(modifier = Modifier.width(16.dp))
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    }
                }
                val models = availableModels
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(models) { modelInfo ->
                        VisualGridItem(
                            title = modelInfo.name,
                            imageUrl = modelInfo.imageUrl,
                            isSelected = model == modelInfo.name,
                            onClick = {
                                model = modelInfo.name
                                // Prefill RAM/storage from the local spec catalog
                                DeviceCatalog.specFor(brand, modelInfo.name)?.let { s ->
                                    ram = s.defaultRam
                                    storage = s.defaultStorage
                                }
                                currentSubStep = 2
                            },
                        )
                    }
                }

                // Fallback for models not listed in the catalog
                if (showManualModel) {
                    OutlinedTextField(
                        value = manualModel,
                        onValueChange = { manualModel = it },
                        label = { Text("Model name (e.g. Galaxy S20)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                    )
                    Button(
                        onClick = {
                            model = manualModel.trim()
                            currentSubStep = 2
                        },
                        enabled = manualModel.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("Continue with \"${manualModel.trim()}\"")
                    }
                } else {
                    TextButton(
                        onClick = { showManualModel = true },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Model list me nahi mila? Naam likh kar enter karo")
                    }
                }
                TextButton(
                    onClick = { currentSubStep = 0 },
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text("Back to Brands")
                }
            }
            2 -> { // Final Details & Specs
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Selection Summary
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Selected Device", style = MaterialTheme.typography.labelSmall)
                                Text("$brand $model", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { currentSubStep = 0 }) {
                                Text("Change")
                            }
                        }
                    }

                    // IMEI Input with Scanner
                    OutlinedTextField(
                        value = imei1,
                        onValueChange = { 
                            // Allow any characters up to 15 length for testing
                            if (it.length <= 15) {
                                imei1 = it
                                if (it.length == 15) onImeiEntered(it)
                            }
                        },
                        label = { Text("IMEI 1 *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isFetchingImei) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                IconButton(onClick = { showScanner = true }) {
                                    Icon(Icons.Rounded.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        },
                    )

                    // Model-specific RAM/storage options when known
                    val modelSpec = remember(brand, model) { DeviceCatalog.specFor(brand, model) }
                    val ramOptions = modelSpec?.ramOptions?.let { opts ->
                        if (ram.isNotBlank() && ram !in opts) opts + ram else opts
                    } ?: DeviceCatalog.ramOptions
                    val storageOptions = modelSpec?.storageOptions?.let { opts ->
                        if (storage.isNotBlank() && storage !in opts) opts + storage else opts
                    } ?: DeviceCatalog.storageOptions

                    // Other Specs
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DropdownSelector(
                            label = "RAM",
                            options = ramOptions,
                            selected = ram,
                            onSelect = { ram = it },
                            modifier = Modifier.weight(1f),
                        )
                        DropdownSelector(
                            label = "Storage",
                            options = storageOptions,
                            selected = storage,
                            onSelect = { storage = it },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    DropdownSelector(
                        label = "Color",
                        options = DeviceCatalog.colors,
                        selected = color,
                        onSelect = { color = it },
                    )
                    
                    OutlinedTextField(
                        value = serialNumber,
                        onValueChange = { serialNumber = it },
                        label = { Text("Serial Number (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PremiumButton(
                        text = buttonText,
                        onClick = {
                            onSubmit(DeviceCreate(brand, model, variant, color, storage, ram, imei1, imei2, serialNumber))
                        },
                        isLoading = isLoading,
                        enabled = (imei1.length == 15) && ram.isNotBlank() && storage.isNotBlank(), // Dummy 15-digit allowed
                    )

                    errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(value = false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
