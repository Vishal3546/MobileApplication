package com.mobile.app.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AppVersionInfo(
    val versionCode: Int,
    val versionName: String,
    val downloadUrl: String,
    val releaseNotes: String? = null
)

@Composable
fun DirectInAppUpdateDialog(
    versionInfoUrl: String = "https://raw.githubusercontent.com/Vishal3546/MobileApplication/main/version.json"
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var versionInfo by remember { mutableStateOf<AppVersionInfo?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(versionInfoUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.requestMethod = "GET"

                if (connection.responseCode == 200) {
                    val jsonStr = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(jsonStr)
                    val serverVersionCode = json.optInt("versionCode", 0)
                    val serverVersionName = json.optString("versionName", "")
                    val apkUrl = json.optString("downloadUrl", "https://github.com/kaushal-mobilebiz/MobileApplication")
                    val notes = json.optString("releaseNotes", "Bug fixes and performance improvements")

                    if (serverVersionCode > BuildConfig.VERSION_CODE) {
                        versionInfo = AppVersionInfo(
                            versionCode = serverVersionCode,
                            versionName = serverVersionName,
                            downloadUrl = apkUrl,
                            releaseNotes = notes
                        )
                        withContext(Dispatchers.Main) {
                            showDialog = true
                        }
                    }
                }
            } catch (e: Exception) {
                // Silently ignore network errors during background update check
            }
        }
    }

    if (showDialog && versionInfo != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.SystemUpdate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "New Update Available!",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Version ${versionInfo!!.versionName} is ready to install.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!versionInfo!!.releaseNotes.isNull_or_empty()) {
                        Text(
                            text = versionInfo!!.releaseNotes!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo!!.downloadUrl))
                        context.startActivity(intent)
                        showDialog = false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Update Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Later")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
