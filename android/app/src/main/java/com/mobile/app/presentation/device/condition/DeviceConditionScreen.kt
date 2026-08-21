package com.mobile.app.presentation.device.condition

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.domain.model.device.ConditionStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceConditionScreen(
    deviceId: String = "placeholder_id",
    viewModel: DeviceConditionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var batteryHealth by remember { mutableStateOf(100f) }
    var displayCondition by remember { mutableStateOf(ConditionStatus.GOOD) }
    var bodyCondition by remember { mutableStateOf(ConditionStatus.GOOD) }
    var cameraCondition by remember { mutableStateOf(ConditionStatus.GOOD) }
    var speakerCondition by remember { mutableStateOf(ConditionStatus.GOOD) }
    var microphoneCondition by remember { mutableStateOf(ConditionStatus.GOOD) }
    var chargingCondition by remember { mutableStateOf(ConditionStatus.GOOD) }
    var biometricStatus by remember { mutableStateOf(ConditionStatus.GOOD) }
    var networkLock by remember { mutableStateOf(false) }
    var originalBill by remember { mutableStateOf(false) }
    var box by remember { mutableStateOf(false) }
    var charger by remember { mutableStateOf(false) }
    var accessories by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is DeviceConditionUiState.Success) {
            onNavigateBack()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Device Condition", style = MaterialTheme.typography.titleLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Battery Health: ${batteryHealth.toInt()}%")
            Slider(
                value = batteryHealth,
                onValueChange = { batteryHealth = it },
                valueRange = 0f..100f
            )
            
            ConditionDropdown("Display", displayCondition) { displayCondition = it }
            ConditionDropdown("Body", bodyCondition) { bodyCondition = it }
            ConditionDropdown("Camera", cameraCondition) { cameraCondition = it }
            ConditionDropdown("Speaker", speakerCondition) { speakerCondition = it }
            ConditionDropdown("Microphone", microphoneCondition) { microphoneCondition = it }
            ConditionDropdown("Charging", chargingCondition) { chargingCondition = it }
            ConditionDropdown("Biometric", biometricStatus) { biometricStatus = it }
            
            Row {
                Checkbox(checked = networkLock, onCheckedChange = { networkLock = it })
                Text("Network Locked", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
            }
            Row {
                Checkbox(checked = originalBill, onCheckedChange = { originalBill = it })
                Text("Original Bill", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
            }
            Row {
                Checkbox(checked = box, onCheckedChange = { box = it })
                Text("Box included", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
            }
            Row {
                Checkbox(checked = charger, onCheckedChange = { charger = it })
                Text("Charger included", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
            }
            Row {
                Checkbox(checked = accessories, onCheckedChange = { accessories = it })
                Text("Accessories included", modifier = Modifier.padding(start = 8.dp, top = 12.dp))
            }
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.createCondition(
                        deviceId,
                        batteryHealth.toInt(),
                        displayCondition,
                        bodyCondition,
                        cameraCondition,
                        speakerCondition,
                        microphoneCondition,
                        chargingCondition,
                        biometricStatus,
                        networkLock,
                        originalBill,
                        box,
                        charger,
                        accessories,
                        notes.takeIf { it.isNotBlank() }
                    )
                },
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                enabled = uiState !is DeviceConditionUiState.Loading
            ) {
                Text("Submit Condition")
            }

            if (uiState is DeviceConditionUiState.Loading) {
                CircularProgressIndicator()
            }
            if (uiState is DeviceConditionUiState.Error) {
                Text((uiState as DeviceConditionUiState.Error).message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionDropdown(label: String, selected: ConditionStatus, onSelected: (ConditionStatus) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth().padding(vertical = 4.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ConditionStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.name) },
                    onClick = {
                        onSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}
