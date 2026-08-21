package com.mobile.app.presentation.kyc

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.data.remote.dto.UploadKycRequestDto
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycUploadScreen(
    customerId: String,
    onNavigateBack: () -> Unit,
    viewModel: KycViewModel = hiltViewModel()
) {
    val actionState by viewModel.actionState.collectAsState()
    var idType by remember { mutableStateOf("NATIONAL_ID") }
    var idNumber by remember { mutableStateOf("") }
    // Note: MediaIds would come from MediaUpload flow (not implemented deeply here for brevity)

    LaunchedEffect(actionState) {
        if (actionState is KycActionState.Success) {
            viewModel.resetActionState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Upload KYC Document") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            OutlinedTextField(
                value = idType,
                onValueChange = { idType = it },
                label = { Text("ID Type (e.g. NATIONAL_ID, PASSPORT)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = idNumber,
                onValueChange = { idNumber = it },
                label = { Text("ID Number") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("In a full implementation, file pickers/CameraX would upload front, back, and photo, and receive mediaIds before submission.")

            Spacer(modifier = Modifier.height(24.dp))

            if (actionState is KycActionState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        viewModel.uploadKyc(
                            customerId = UUID.fromString(customerId),
                            request = UploadKycRequestDto(
                                idType = idType,
                                idNumber = idNumber,
                                frontMediaId = null,
                                backMediaId = null,
                                photoMediaId = null
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = idNumber.isNotBlank()
                ) {
                    Text("Submit KYC")
                }
            }

            if (actionState is KycActionState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (actionState as KycActionState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
