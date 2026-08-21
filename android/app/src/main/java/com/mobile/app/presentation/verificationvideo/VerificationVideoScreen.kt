package com.mobile.app.presentation.verificationvideo

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationVideoScreen(
    customerId: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasPermissions = permissions[Manifest.permission.CAMERA] == true && 
                             permissions[Manifest.permission.RECORD_AUDIO] == true
        }
    )

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            launcher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Verification Video") }) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!hasPermissions) {
                Text("Camera and Audio permissions are required to record verification video.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }) {
                    Text("Grant Permissions")
                }
            } else {
                Text("CameraX Preview Surface goes here.")
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { 
                    // Record and upload to MediaApi
                    // Return mediaId
                    onNavigateBack()
                }) {
                    Text("Record Video & Upload")
                }
            }
        }
    }
}
