package com.example.senaisp.aplicativomedico.screens

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.gson.JsonElement
import com.twilio.video.* // Twilio SDK
import java.lang.Exception
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.twilio.video.ConnectOptions
import com.twilio.video.RemoteParticipant
import com.twilio.video.Room
import com.twilio.video.TwilioException
import com.twilio.video.Video
import com.twilio.video.VideoView
import com.twilio.video.LocalVideoTrack
import com.twilio.video.LocalAudioTrack
import com.twilio.video.CameraCapturer
import com.twilio.video.Camera2Capturer
import tvi.webrtc.Camera2Enumerator


@SuppressLint("UnrememberedMutableState")
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
    var hasAudioPermission by remember { mutableStateOf(false) }
    var hasCameraPermissionRemote by remember { mutableStateOf(true) }
    var isRemoteCameraOff by remember { mutableStateOf(false) }


    var jwtToken by remember { mutableStateOf<String?>(null) }
    var identityState by remember { mutableStateOf<String?>(null) }
    var serverRoom by remember { mutableStateOf<String?>(null) }
    var loading by mutableStateOf(false)




    // Preview da câmera
    // PreviewView para CameraX (mostra preview local do CameraX)


    // --- Twilio render views (local e remoto) ---
    // VideoView do Twilio para renderizar tracks (local e remoto)
    val localVideoView = remember { VideoView(context) }    // mostra sua própria câmera (opcional)
    val remoteVideoView = remember { VideoView(context) }   // mostra a câmera do outro participante

// --- Local tracks (iniciadas quando permissão concedida) ---
    var localVideoTrack by remember { mutableStateOf<LocalVideoTrack?>(null) }
    var localAudioTrack by remember { mutableStateOf<LocalAudioTrack?>(null) }


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

    // Launcher para solicitar permissão do microfone
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            Log.i("VIDEOCHAMADA", "Permissão do microfone concedida")
        } else {
            Log.e("VIDEOCHAMADA", "Permissão do microfone negada")
        }
    }

    // Verificar permissão da câmera
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (!hasAudioPermission) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Configurar câmera quando permissão for concedida


    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            try {
                if (localVideoTrack == null) {

                    // CORREÇÃO: descobrir o ID correto da camera frontal
                    val enumerator = Camera2Enumerator(context)
                    val cameraId = enumerator.deviceNames.firstOrNull {
                        enumerator.isFrontFacing(it)
                    } ?: throw Exception("Nenhuma câmera frontal encontrada")

                    val cameraCapturer = Camera2Capturer(
                        context,
                        cameraId,   // agora usa o ID REAL
                        null
                    )

                    localVideoTrack = LocalVideoTrack.create(
                        context,
                        true,
                        cameraCapturer
                    )

                    localVideoTrack?.addSink(localVideoView)

                }

                if (localAudioTrack == null) {
                    localAudioTrack = LocalAudioTrack.create(context, true)
                }

            } catch (e: Exception) {
                Log.e("VIDEOCHAMADA", "Erro criando local tracks: ${e.message}")
                errorMessage = "Erro ao iniciar áudio/vídeo locais: ${e.message}"
            }
        }
    }



    // Reage às mudanças de toggle e habilita/desabilita tracks reais
    LaunchedEffect(isCameraOff, isMicMuted, localVideoTrack, localAudioTrack) {
        // câmera local on/off
        localVideoTrack?.let { track ->
            try {
                track.enable(!isCameraOff) // se SDK tiver enable(Boolean). Caso não exista -> publicar/unpublish
            } catch (e: Exception) {
                Log.e("VIDEOCHAMADA", "Não foi possível alternar vídeo: ${e.message}")
            }
        }

        // microfone local mudo/desmudo
        localAudioTrack?.let { at ->
            try {
                at.enable(!isMicMuted)
            } catch (e: Exception) {
                Log.e("VIDEOCHAMADA", "Não foi possível alternar áudio: ${e.message}")
            }
        }
    }




    fun setRemoteVideo(participant: RemoteParticipant) {
        participant.setListener(object : RemoteParticipant.Listener {
            override fun onAudioTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {

            }

            override fun onAudioTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {

            }

            override fun onAudioTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {

            }

            override fun onAudioTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                twilioException: TwilioException
            ) {

            }

            override fun onAudioTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication,
                remoteAudioTrack: RemoteAudioTrack
            ) {

            }

            override fun onVideoTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {

            }

            override fun onVideoTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {

            }

            override fun onVideoTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                // Renderiza o vídeo remoto no VideoView do Twilio
                remoteVideoTrack.addSink(remoteVideoView)
            }

            override fun onVideoTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                twilioException: TwilioException
            ) {

            }

            override fun onVideoTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication,
                remoteVideoTrack: RemoteVideoTrack
            ) {
                remoteVideoTrack.removeSink(remoteVideoView)
            }


            override fun onDataTrackPublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {

            }

            override fun onDataTrackUnpublished(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication
            ) {

            }

            override fun onDataTrackSubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {

            }

            override fun onDataTrackSubscriptionFailed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                twilioException: TwilioException
            ) {

            }

            override fun onDataTrackUnsubscribed(
                remoteParticipant: RemoteParticipant,
                remoteDataTrackPublication: RemoteDataTrackPublication,
                remoteDataTrack: RemoteDataTrack
            ) {

            }

            override fun onAudioTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {

            }

            override fun onAudioTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteAudioTrackPublication: RemoteAudioTrackPublication
            ) {

            }

            override fun onVideoTrackEnabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {

            }

            override fun onVideoTrackDisabled(
                remoteParticipant: RemoteParticipant,
                remoteVideoTrackPublication: RemoteVideoTrackPublication
            ) {

            }
        })
    }


        var currentRoom: Room? = null

        fun connectToTwilio(jwt: String, roomName: String, identity: String? = null) {
            try {
                isConnecting = true

                val connectOptions = ConnectOptions.Builder(jwt)
                    .roomName(roomName)
                    .apply {
                        localAudioTrack?.let { this.audioTracks(listOf(it)) }
                        localVideoTrack?.let { this.videoTracks(listOf(it)) }
                    }
                    .build()

                val roomListener = object : Room.Listener {
                    override fun onConnected(room: Room) {
                        isConnected = true
                        isConnecting = false
                        currentRoom = room
                    }

                    override fun onConnectFailure(room: Room, e: TwilioException) {
                        isConnected = false
                        isConnecting = false
                        errorMessage = "Falha ao conectar: ${e.message}"
                    }

                    override fun onReconnecting(
                        room: Room,
                        twilioException: TwilioException
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onReconnected(room: Room) {
                        TODO("Not yet implemented")
                    }

                    override fun onDisconnected(room: Room, e: TwilioException?) {
                        isConnected = false
                        isConnecting = false
                        currentRoom = null
                    }

                    override fun onParticipantConnected(room: Room, participant: RemoteParticipant) {
                        Log.i("TWILIO", "Participante entrou: ${participant.identity}")
                        setRemoteVideo(participant)
                    }
                    override fun onParticipantDisconnected(room: Room, participant: RemoteParticipant) {}
                    override fun onRecordingStarted(room: Room) {}
                    override fun onRecordingStopped(room: Room) {}
                }

                currentRoom = Video.connect(context, connectOptions, roomListener)

            } catch (e: kotlin.Exception) {
                isConnecting = false
                errorMessage = "Erro conectar Twilio: ${e.message}"
            }
        }

        fun disconnect() {
            currentRoom?.disconnect()
            currentRoom = null
            isConnected = false
        }


    // Gerar nome da sala se não fornecido
    val currentRoomName = roomName ?: "sala-${UUID.randomUUID().toString().take(8)}"

    // Função para buscar token do backend (padrão Retrofit/Conexao do projeto)
    fun fetchTokenFromServer(room: String) {
        loading = true
        errorMessage = null
        coroutineScope.launch(Dispatchers.IO) {
            val bearer = SessionManager.getBearerToken(context)
            val tokenService = Conexao.getTokenService()
            try {
                val response = tokenService.requestToken(bearer, mapOf("room" to room)).execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    // Extrair token_str / identity / room de forma segura
                    var tokenStr: String? = null
                    var identityResp: String? = null
                    var roomResp: String? = null

                    // 1) se body.token é JsonElement (objeto ou string)
                    val tokenElement: JsonElement? = body?.token
                    if (tokenElement != null) {
                        try {
                            if (tokenElement.isJsonPrimitive) {
                                // token foi retornado como string (caso raro)
                                tokenStr = tokenElement.asString
                            } else if (tokenElement.isJsonObject) {
                                val obj = tokenElement.asJsonObject
                                if (obj.has("token")) tokenStr = obj.get("token").asString
                                if (obj.has("identity")) identityResp = obj.get("identity").asString
                                if (obj.has("room")) roomResp = obj.get("room").asString
                                // fallback: identity_user etc
                                if (identityResp == null) {
                                    if (obj.has("identity_user")) identityResp = obj.get("identity_user").asString
                                }
                            }
                        } catch (_: Exception) {}
                    }

                    // 2) fallback para campos diretos no envelope
                    if (tokenStr == null) {
                        // às vezes o JWT pode estar dentro de outros campos
                        tokenStr = body?.identity // (não é comum) - mantenha apenas como fallback
                    }

                    identityResp = identityResp ?: body?.identity
                    roomResp = roomResp ?: body?.room

                    withContext(Dispatchers.Main) {
                        jwtToken = tokenStr
                        identityState = identityResp
                        serverRoom = roomResp ?: room
                        loading = false
                    }

                    // se obtivemos jwtToken, conectamos automaticamente
                    if (!tokenStr.isNullOrBlank()) {
                        withContext(Dispatchers.Main) {
                            connectToTwilio(tokenStr, serverRoom ?: room, identityState)
                        }
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
                Box(modifier = Modifier.fillMaxWidth()
                    .weight(1f)) {

                    when {
                        !hasCameraPermissionRemote -> {
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

                        isRemoteCameraOff -> {
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
                            // Exibe o vídeo REMOTO ocupando 100% do card
                            AndroidView(
                                factory = { remoteVideoView },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }

                    // Nome e status no canto superior esquerdo do card (MÉDICO)
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Médico",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        Text(
                            text = when {
                                !hasCameraPermissionRemote -> "Sem permissão"
                                isRemoteCameraOff -> "Câmera desligada"
                                else -> "Câmera ligada"
                            },
                            fontSize = 12.sp,
                            color = when {
                                !hasCameraPermissionRemote -> Color.Yellow
                                isRemoteCameraOff -> Color.Red
                                else -> Color.Green
                            }
                        )
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
                                // Exibe o vídeo local (VideoView do Twilio)
                                AndroidView(
                                    factory = { localVideoView },
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
                    identityState?.let {
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
