package com.mobile.app.presentation.signature

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    customerId: String,
    onNavigateBack: () -> Unit,
    onSignatureCaptured: (String) -> Unit // returns mediaId ideally
) {
    var paths by remember { mutableStateOf(listOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Capture Signature") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            Text("Please sign below:", modifier = Modifier.padding(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
                    .background(Color.LightGray)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val newPath = Path().apply { moveTo(offset.x, offset.y) }
                                    currentPath = newPath
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newPath = Path().apply {
                                        currentPath?.let { addPath(it) }
                                        lineTo(change.position.x, change.position.y)
                                    }
                                    currentPath = newPath
                                },
                                onDragEnd = {
                                    currentPath?.let { paths = paths + it }
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    paths.forEach { path ->
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                    currentPath?.let { path ->
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Button(onClick = { paths = emptyList(); currentPath = null }) {
                    Text("Clear")
                }
                
                Button(onClick = {
                    if (paths.isNotEmpty()) {
                        // In a real app: draw paths to a Bitmap Canvas, save to context.cacheDir, upload to MediaApi, return mediaId
                        onSignatureCaptured("dummy-signature-media-id")
                    }
                }) {
                    Text("Confirm Signature")
                }
            }
        }
    }
}
