package com.example.senaisp.aplicativomedico.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.service.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

@Composable
fun VideoChamadaScreen(navegacao: NavHostController?, roomName: String? = null) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isMicMuted by remember { mutableStateOf(false) }
    var isCameraOff by remember { mutableStateOf(false) }
    var isConnecting by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    var identity by remember { mutableStateOf<String?>(null) }
    var serverRoom by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // Preview da câmera
    val previewView = remember { PreviewView(context) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val coroutineScope = rememberCoroutineScope()

    // Launcher para solicitar permissão da câmera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            Log.i("VIDEOCHAMADA", "Permissão da câmera concedida")
        } else {
            Log.e("VIDEOCHAMADA", "Permissão da câmera negada")
        }
    }

    // Verificar permissão da câmera
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Configurar câmera quando permissão for concedida
    LaunchedEffect(hasCameraPermission, isCameraOff) {
        if (hasCameraPermission && !isCameraOff) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = CameraPreview.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
                Log.i("VIDEOCHAMADA", "Câmera configurada com sucesso")
            } catch (e: Exception) {
                Log.e("VIDEOCHAMADA", "Erro ao configurar câmera: ${e.message}")
            }
        } else if (isCameraOff) {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        }
    }

    // Gerar nome da sala se não fornecido
    val currentRoomName = roomName ?: "sala-${UUID.randomUUID().toString().take(8)}"

    // Função para buscar token do backend (padrão Retrofit/Conexao do projeto)
    fun fetchTokenFromServer(room: String) {
        loading = true
        errorMessage = null
        coroutineScope.launch(Dispatchers.IO) {
            val bearer = SessionManager.getBearerToken(context)
            val tokenService = Conexao().getTokenService()
            try {
                val response = tokenService.requestToken(bearer, mapOf("room" to room)).execute()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    // Tenta extrair token de várias formas
                    var identityResp: String? = null
                    var roomResp: String? = null

                    // Normaliza o campo token que pode vir como String ou como objeto (Map)
                    val tokenField = body.token
                    when (tokenField) {
                        is String -> {
                            // token field is a plain string (jwt) - we ignore it here; keep identity/room parsing
                        }
                        is Map<*, *> -> {
                            try {
                                val map = tokenField as Map<*, *>
                                identityResp = (map["identity"] ?: map["identity_user"] ?: map["Identity"])?.toString()
                                roomResp = (map["room"] ?: map["Room"])?.toString()
                            } catch (_: Exception) {
                                // ignore
                            }
                        }
                        else -> {
                            // other shapes: ignored for now
                        }
                    }

                    // fallback para campos diretos no envelope
                    identityResp = identityResp ?: body.identity
                    roomResp = roomResp ?: body.room

                    withContext(Dispatchers.Main) {
                        identity = identityResp
                        serverRoom = roomResp ?: currentRoomName
                        loading = false
                    }

                } else {
                    val err = response.errorBody()?.string()
                    withContext(Dispatchers.Main) {
                        errorMessage = "Erro ao obter token: ${response.code()}" + (if (!err.isNullOrBlank()) " - ${err.take(200)}" else "")
                        loading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Erro de conexão: ${e.message}"
                    loading = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Interface existente (do arquivo original) — simplificado para foco no token
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF7986CB))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Dois cards empilhados com tamanho igual usando weight
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    // Conteúdo do card do médico
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.Black, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Dr. Souza",
                                tint = Color.White,
                                modifier = Modifier.size(120.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Dr. Souza",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when {
                                isConnecting -> "Conectando..."
                                isConnected -> "Conectado - Sala: ${serverRoom ?: currentRoomName}"
                                errorMessage != null -> "Erro na conexão"
                                else -> "Aguardando conexão"
                            },
                            fontSize = 14.sp,
                            color = when {
                                isConnecting -> Color.Yellow
                                isConnected -> Color.Green
                                errorMessage != null -> Color.Red
                                else -> Color.Gray
                            }
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { /* Opções */ }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Mais opções", tint = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        when {
                            !hasCameraPermission -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Gray, RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = "Sem permissão",
                                            tint = Color.White,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = "Sem permissão de câmera", color = Color.White)
                                    }
                                }
                            }
                            isCameraOff -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.VideocamOff,
                                            contentDescription = "Câmera desligada",
                                            tint = Color.White,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = "Câmera desligada", color = Color.White)
                                    }
                                }
                            }
                            else -> {
                                AndroidView(
                                    factory = { previewView },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }

                        // Nome e status no canto superior esquerdo do card
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Você",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = when {
                                    !hasCameraPermission -> "Sem permissão"
                                    isCameraOff -> "Câmera desligada"
                                    else -> "Câmera ligada"
                                },
                                fontSize = 12.sp,
                                color = when {
                                    !hasCameraPermission -> Color.Yellow
                                    isCameraOff -> Color.Red
                                    else -> Color.Green
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Barra de controles fora dos cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (hasCameraPermission) {
                                isCameraOff = !isCameraOff
                                Log.i("VIDEOCHAMADA", "Câmera ${if (isCameraOff) "desligada" else "ligada"}")
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (isCameraOff) Color.Red.copy(alpha = 0.85f) else Color.Green.copy(alpha = 0.85f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                            contentDescription = if (isCameraOff) "Ligar câmera" else "Desligar câmera",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    IconButton(
                        onClick = { isMicMuted = !isMicMuted },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (isMicMuted) Color.Red.copy(alpha = 0.85f) else Color.Green.copy(alpha = 0.85f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMicMuted) "Microfone mutado" else "Microfone ligado",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botões de controle: buscar token e conectar (atualmente apenas busca token)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { fetchTokenFromServer(currentRoomName) }, enabled = !loading) {
                        Text(if (loading) "Buscando token..." else "Buscar Token e Conectar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    if (loading) {
                        Text("Conectando…", color = Color.Yellow)
                    }
                    if (!errorMessage.isNullOrBlank()) {
                        Text("Erro: $errorMessage", color = Color.Red)
                    }
                    identity?.let {
                        Text("Conectado como: $it", color = Color.White)
                    }
                    serverRoom?.let {
                        Text("Sala: $it", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoChamadaPreview() {
    VideoChamadaScreen(navegacao = null)
}
