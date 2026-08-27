package com.mobile.app.presentation.device.media

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.mobile.app.domain.model.device.DeviceMediaType
import java.io.File
import java.util.concurrent.Executors

@Composable
fun DeviceMediaScreen(
    viewModel: DeviceMediaViewModel = hiltViewModel(),
    deviceId: String = "placeholder_id"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by viewModel.uiState.collectAsState()
    val mediaList by viewModel.mediaList.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    var showCamera by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(DeviceMediaType.FRONT) }

    LaunchedEffect(deviceId) {
        viewModel.loadMedia(deviceId)
    }

    if (showCamera) {
        if (hasCameraPermission) {
            CameraView(
                onImageCaptured = { file ->
                    showCamera = false
                    viewModel.uploadMedia(deviceId, selectedType, file)
                },
                onError = { /* Log omitted for security */ },
                onCancel = { showCamera = false }
            )
        } else {
            // Permission Rationale
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera permission is required to take device photos.")
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
                Button(onClick = { showCamera = false }) {
                    Text("Cancel")
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Device Media", style = MaterialTheme.typography.titleLarge)

            // Select Media Type
            Text("Select View Type:")
            // Dropdown placeholder or simple list
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { selectedType = DeviceMediaType.FRONT; showCamera = true }) {
                    Text("Capture Front")
                }
                Button(onClick = { selectedType = DeviceMediaType.BACK; showCamera = true }) {
                    Text("Capture Back")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Status messages
            when (val state = uiState) {
                is DeviceMediaUiState.Uploading -> Text("Uploading ${state.type}...", color = Color.Blue)
                is DeviceMediaUiState.Linking -> Text("Linking ${state.type}...", color = Color.Blue)
                is DeviceMediaUiState.UploadSuccess -> Text("Uploaded successfully!", color = Color.Green)
                is DeviceMediaUiState.LinkFailed -> {
                    Column {
                        Text("Linking failed for ${state.type}: ${state.message}", color = Color.Red)
                        Button(onClick = { viewModel.linkMedia(deviceId, state.mediaId, state.type) }) {
                            Text("Retry Linking")
                        }
                    }
                }
                is DeviceMediaUiState.Error -> Text("Error: ${state.message}", color = Color.Red)
                else -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Uploaded Media:")
            mediaList.forEach { media ->
                Text("- ${media.type}: ${media.url}")
            }
        }
    }
}

@Composable
fun CameraView(
    onImageCaptured: (File) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    
    var capturedFile by remember { mutableStateOf<File?>(null) }
    
    if (capturedFile != null) {
        // Review Screen
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Photo Captured. Review:")
            // We would show the image here with Coil/Glide in a real app
            // AsyncImage(model = capturedFile, contentDescription = null)
            Text(capturedFile!!.name)
            
            Row(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { capturedFile?.delete(); capturedFile = null }) {
                    Text("Retake")
                }
                Button(onClick = { onImageCaptured(capturedFile!!) }) {
                    Text("Confirm & Upload")
                }
            }
        }
    } else {
        // Preview view
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = ContextCompat.getMainExecutor(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        imageCapture = ImageCapture.Builder().build()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (exc: Exception) {
                            // Log omitted for security
                        }
                    }, executor)
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(onClick = {
                    val photoFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                capturedFile = photoFile
                            }

                            override fun onError(exc: ImageCaptureException) {
                                onError(exc)
                            }
                        }
                    )
                }) {
                    Text("Capture")
                }
            }
        }
    }
}
