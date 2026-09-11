package com.mobile.app.core.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
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
import androidx.core.content.FileProvider
import com.mobile.app.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
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
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0) }
    var downloadError by remember { mutableStateOf<String?>(null) }

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
                    val apkUrl = json.optString("downloadUrl", "")
                    val notes = json.optString("releaseNotes", "Bug fixes and performance improvements")

                    if (serverVersionCode > BuildConfig.VERSION_CODE && apkUrl.isNotBlank()) {
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
            onDismissRequest = { 
                if (!isDownloading) showDialog = false 
            },
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
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Version ${versionInfo!!.versionName} is ready to install.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!versionInfo!!.releaseNotes.isNullOrBlank()) {
                        Text(
                            text = versionInfo!!.releaseNotes!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isDownloading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = downloadProgress / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Downloading update... $downloadProgress%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    if (downloadError != null) {
                        Text(
                            text = downloadError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isDownloading) {
                            isDownloading = true
                            downloadError = null
                            downloadAndInstallApk(
                                context = context,
                                apkUrl = versionInfo!!.downloadUrl,
                                onProgress = { progress -> downloadProgress = progress },
                                onError = { error ->
                                    isDownloading = false
                                    downloadError = error
                                },
                                onSuccess = {
                                    isDownloading = false
                                    showDialog = false
                                }
                            )
                        }
                    },
                    enabled = !isDownloading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isDownloading) "Downloading..." else "Update Now")
                }
            },
            dismissButton = {
                if (!isDownloading) {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Later")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

private fun downloadAndInstallApk(
    context: Context,
    apkUrl: String,
    onProgress: (Int) -> Unit,
    onError: (String) -> Unit,
    onSuccess: () -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL(apkUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.connect()

            val fileLength = connection.contentLength
            val apkFile = File(context.cacheDir, "update.apk")

            connection.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val data = ByteArray(4096)
                    var total: Long = 0
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        total += count.toLong()
                        if (fileLength > 0) {
                            val progress = (total * 100 / fileLength).toInt()
                            withContext(Dispatchers.Main) {
                                onProgress(progress)
                            }
                        }
                        output.write(data, 0, count)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                onSuccess()
                installApk(context, apkFile)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError("Download failed: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
}

private fun installApk(context: Context, apkFile: File) {
    val authority = "${context.packageName}.fileprovider"
    val apkUri: Uri = FileProvider.getUriForFile(context, authority, apkFile)

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(apkUri, "application/vnd.android.package-archive")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
