package com.example.senaisp.aplicativomedico.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CamPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.core.content.ContextCompat

@Composable
fun Chamada(navegacao: NavHostController?) {

    // States for camera and microphone
    val cameraOn = remember { mutableStateOf(true) }
    val micOn = remember { mutableStateOf(true) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF7E83C9)) // purple background similar to the reference
    ) {
        // Reuse top header if available in the project
        SalaConsulta(navegacao)

        // Two equal cards that occupy available space
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top card (camera preview placeholder) - MEDICO
            Box(modifier = Modifier
                .weight(1f)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    // Camera preview area: fill entire card
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(if (cameraOn.value) Color.Black else Color(0xFFBDBDBD))
                    ) {
                        // When camera is off, show a small icon at center
                        if (!cameraOn.value) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Camera off",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .size(56.dp)
                                    .align(Alignment.Center)
                            )
                        }

                        // Small label in bottom-right corner
                        Text(
                            text = "MEDICO",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Bottom card (camera preview placeholder) - VOCÊ
            Box(modifier = Modifier
                .weight(1f)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (cameraOn.value) {
                            CameraXPreview(lensFacing = CameraSelector.LENS_FACING_FRONT, modifier = Modifier.fillMaxSize())
                        } else {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFBDBDBD)))
                        }

                        // Small label in bottom-right corner
                        Text(
                            text = "VOCÊ",
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                        )
                    }
                }
            }
        }

        // Bottom control bar (rounded white panel with icons)
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: toggle camera
                    IconButton(onClick = { cameraOn.value = !cameraOn.value }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = if (cameraOn.value) "Camera on" else "Camera off",
                            tint = if (cameraOn.value) Color(0xFF616161) else Color(0xFFB71C1C),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Middle group: mic toggle and big red end-call button
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                        IconButton(onClick = { micOn.value = !micOn.value }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = if (micOn.value) "Mic on" else "Mic off",
                                tint = if (micOn.value) Color(0xFF616161) else Color(0xFFB71C1C),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // End call big red circular button
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFEF5350),
                            modifier = Modifier.size(56.dp)
                        ) {
                            IconButton(onClick = { navegacao?.navigateUp() }, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = "End Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Right: placeholder to keep spacing (could be chat or more)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
fun CameraXPreview(lensFacing: Int = CameraSelector.LENS_FACING_FRONT, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }

        val executor = ContextCompat.getMainExecutor(ctx)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = CamPreview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            } catch (e: Exception) {
                // ignore or log
            }
        }, executor)

        previewView
    }, modifier = modifier)

    DisposableEffect(Unit) {
        onDispose {
            try {
                val provider = cameraProviderFuture.get()
                provider.unbindAll()
            } catch (_: Exception) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChamadaPreview() {
    Chamada(navegacao = null)
}