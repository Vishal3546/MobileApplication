package com.mobile.app.presentation.device.inspection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.domain.model.device.DeviceInspectionCreate
import com.mobile.app.domain.model.device.InspectionStatus

@Composable
fun DeviceInspectionScreen(
    deviceId: String = "placeholder_id",
    viewModel: DeviceInspectionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is DeviceInspectionUiState.Success) {
            onNavigateBack()
        }
    }

    var display by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var touch by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var camera by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var speaker by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var microphone by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var charging by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    
    var wifi by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var bluetooth by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var sim by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var fingerprint by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var faceId by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var battery by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var flash by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var vibration by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    var network by remember { mutableStateOf(InspectionStatus.NOT_TESTED) }
    
    var notes by remember { mutableStateOf("") }
    
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Device Inspection", style = MaterialTheme.typography.titleLarge)
            
            if (uiState is DeviceInspectionUiState.Error) {
                Text(
                    text = (uiState as DeviceInspectionUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Mandatory Tests", style = MaterialTheme.typography.titleMedium)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            InspectionRow("Display", display) { display = it }
            InspectionRow("Touch", touch) { touch = it }
            InspectionRow("Camera", camera) { camera = it }
            InspectionRow("Speaker", speaker) { speaker = it }
            InspectionRow("Microphone", microphone) { microphone = it }
            InspectionRow("Charging", charging) { charging = it }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Optional Tests", style = MaterialTheme.typography.titleMedium)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            InspectionRow("Wi-Fi", wifi) { wifi = it }
            InspectionRow("Bluetooth", bluetooth) { bluetooth = it }
            InspectionRow("SIM", sim) { sim = it }
            InspectionRow("Fingerprint", fingerprint) { fingerprint = it }
            InspectionRow("Face ID", faceId) { faceId = it }
            InspectionRow("Battery", battery) { battery = it }
            InspectionRow("Flash", flash) { flash = it }
            InspectionRow("Vibration", vibration) { vibration = it }
            InspectionRow("Network", network) { network = it }
            
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Inspection Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                minLines = 3
            )
            
            Button(
                onClick = {
                    val createDto = DeviceInspectionCreate(
                        display = display,
                        touch = touch,
                        camera = camera,
                        speaker = speaker,
                        microphone = microphone,
                        charging = charging,
                        wifi = wifi,
                        bluetooth = bluetooth,
                        sim = sim,
                        fingerprint = fingerprint,
                        faceId = faceId,
                        battery = battery,
                        flash = flash,
                        vibration = vibration,
                        network = network,
                        notes = notes
                    )
                    viewModel.createInspection(deviceId, createDto)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = uiState !is DeviceInspectionUiState.Loading
            ) {
                if (uiState is DeviceInspectionUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Submit Inspection")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectionRow(
    title: String,
    currentStatus: InspectionStatus,
    onStatusChange: (InspectionStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(title, modifier = Modifier.weight(1f))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val statuses = listOf(InspectionStatus.PASS, InspectionStatus.FAIL, InspectionStatus.NOT_TESTED)
            for (status in statuses) {
                FilterChip(
                    selected = currentStatus == status,
                    onClick = { onStatusChange(status) },
                    label = { Text(status.name) }
                )
            }
        }
    }
}
