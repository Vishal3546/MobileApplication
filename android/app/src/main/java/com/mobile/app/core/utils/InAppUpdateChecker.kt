package com.mobile.app.core.utils

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.appdistribution.FirebaseAppDistribution
import com.mobile.app.BuildConfig

@Composable
fun DirectInAppUpdateDialog() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var updateReleaseNotes by remember { mutableStateOf<String?>(null) }
    var updateVersionName by remember { mutableStateOf<String?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    var isReadyToInstall by remember { mutableStateOf(false) }

    val appDistribution = FirebaseAppDistribution.getInstance()

    LaunchedEffect(Unit) {
        if (!BuildConfig.DEBUG) {
            appDistribution.updateIfNewReleaseAvailable()
                .addOnProgressListener { updateProgress ->
                    isDownloading = true
                    downloadProgress = updateProgress.apkBytesDownloaded.toFloat() / updateProgress.apkFileTotalBytes.toFloat()
                }
                .addOnSuccessListener {
                    // Update is successful or no update is available
                    isDownloading = false
                    isReadyToInstall = false
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    isDownloading = false
                    downloadError = e.message
                }
        }
    }
}

