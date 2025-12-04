package com.example.senaisp.aplicativomedico.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch
import android.util.Log
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.model.MessageItem
import com.example.senaisp.aplicativomedico.model.DadosMensagem
import com.example.senaisp.aplicativomedico.model.ResponseMensagem
import com.example.senaisp.aplicativomedico.service.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Helpers de formatação de data/hora compartilhados (top-level)
private val displayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    .withZone(ZoneId.systemDefault())

fun formatIsoToDisplay(iso: String?): String {
    if (iso.isNullOrBlank()) return ""

    // Tenta várias estratégias de parse para cobrir formatos comuns retornados pela API
    try {
        // 1) Instant/ISO_INSTANT (ex: 2025-12-04T05:56:10.000Z)
        try {
            val instant = Instant.parse(iso)
            return displayFormatter.format(instant)
        } catch (_: Exception) {
            // continue
        }

        // 2) OffsetDateTime (ex: 2025-12-04T05:56:10+00:00)
        try {
            val odt = java.time.OffsetDateTime.parse(iso)
            return displayFormatter.format(odt.toInstant())
        } catch (_: Exception) {
            // continue
        }

        // 3) LocalDateTime com alguns padrões comuns (sem fuso explicitamente)
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm"
        )
        for (p in patterns) {
            try {
                val dtf = DateTimeFormatter.ofPattern(p)
                val ldt = java.time.LocalDateTime.parse(iso, dtf)
                return displayFormatter.format(ldt.atZone(ZoneId.systemDefault()).toInstant())
            } catch (_: Exception) {
                // tentar próximo padrão
            }
        }
    } catch (_: Exception) {
        // fallback abaixo
    }

    // fallback: retorna a string original se não conseguir parsear
    return iso
}

fun nowFormatted(): String = displayFormatter.format(Instant.now())


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun ChatIndividualScreen(
    navegacao: NavHostController?,
    contatoId: String,
    contatoNome: String,
    medicoId: String,
    medicoUserId: String,
    chatId: String
) {
    // Log recebimento dos parâmetros para verificar no Logcat
    Log.d("CHAT_SCREEN", "Abrindo chat - contatoId: $contatoId, contatoNome: $contatoNome, medicoId: $medicoId, medicoUserId: $medicoUserId, chatId: $chatId")

    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var mensagemTexto by remember { mutableStateOf("") }

    // estado para mensagens carregadas
    var mensagens by remember { mutableStateOf(listOf<MessageItem>()) }

    // variável para controlar primeiro carregamento
    var firstLoad by remember { mutableStateOf(true) }

    val focusRequester = remember { FocusRequester() }

    // Função local para buscar mensagens e atualizar o estado
    fun fetchOnce(idInt: Int, onResult: (List<MessageItem>) -> Unit) {
        Conexao.getChatMessageService().getMessagesByChatId(idInt).enqueue(object : Callback<com.example.senaisp.aplicativomedico.model.ResponseMensagens> {
            override fun onResponse(call: Call<com.example.senaisp.aplicativomedico.model.ResponseMensagens>, response: Response<com.example.senaisp.aplicativomedico.model.ResponseMensagens>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.status_code == 200) {
                        onResult(body.mensagens)
                    } else {
                        Log.w("CHAT_SCREEN", "Resposta GET mensagens inválida ou status_code != 200")
                    }
                } else {
                    Log.e("CHAT_SCREEN", "Falha ao buscar mensagens: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<com.example.senaisp.aplicativomedico.model.ResponseMensagens>, t: Throwable) {
                Log.e("CHAT_SCREEN", "Erro ao buscar mensagens: ${t.message}")
            }
        })
    }

    // Polling: busca mensagens repetidamente a cada 2 segundos enquanto a composable estiver ativa
    LaunchedEffect(chatId) {
        val idInt = chatId.toIntOrNull() ?: 0
        if (idInt <= 0) return@LaunchedEffect

        while (isActive) {
            try {
                fetchOnce(idInt) { newMessages ->
                    // Atualiza mensagens apenas se mudou (evita recomposições desnecessárias)
                    if (newMessages != mensagens) {
                        mensagens = newMessages
                    }
                }
            } catch (t: Throwable) {
                Log.e("CHAT_SCREEN", "Erro no polling de mensagens: ${t.message}")
            }
            delay(2000)
        }
    }

    // Auto-scroll inteligente: rola para o fim somente no primeiro load ou se o usuário já estiver no fim da lista
    var prevSize by remember { mutableStateOf(0) }
    LaunchedEffect(mensagens.size) {
        if (mensagens.isEmpty()) return@LaunchedEffect

        // Se for o primeiro load, rolar para o fim
        if (firstLoad) {
            listState.animateScrollToItem(mensagens.lastIndex)
            firstLoad = false
            prevSize = mensagens.size
            return@LaunchedEffect
        }

        // Se as mensagens aumentaram, verificar se o usuário está aproximadamente no fim da lista
        val totalItems = listState.layoutInfo.totalItemsCount
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        val atBottom = lastVisible >= totalItems - 2 // perto do fim

        if (mensagens.size > prevSize && atBottom) {
            listState.animateScrollToItem(mensagens.lastIndex)
        }
        prevSize = mensagens.size
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7986CB))
    ) {
        // Header do chat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navegacao?.popBackStack() }
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Avatar do contato
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = contatoNome,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Botão de videochamada
            IconButton(
                onClick = {
                    navegacao?.navigate("")
                }
            ) {
                Icon(
                    Icons.Default.VideoCall,
                    contentDescription = "Iniciar videochamada",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Área de mensagens
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
        ) {
            // Lista de mensagens (fica acima do campo de input)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = listState,
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 92.dp) // reserve space for input
            ) {
                items(mensagens) { msg ->
                    // Renderiza cada mensagem real
                    MessageRow(msg)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Campo de entrada de mensagem fixo na parte inferior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .imePadding(),
                verticalAlignment = Alignment.Bottom
            ) {
                // Campo de texto
                OutlinedTextField(
                    value = mensagemTexto,
                    onValueChange = { mensagemTexto = it },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp, max = 120.dp)
                        .focusRequester(focusRequester)
                        .onFocusChanged { state ->
                            if (state.isFocused) {
                                coroutineScope.launch {
                                    if (mensagens.isNotEmpty()) listState.animateScrollToItem(mensagens.lastIndex)
                                }
                            }
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7986CB),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    placeholder = {
                        Text(
                            "Mensagem",
                            color = Color.Gray
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { /* Ação da câmera */ }
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Câmera",
                                tint = Color(0xFF7986CB)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                FloatingActionButton(
                    onClick = {
                        // Ação de enviar: agora chamando a API para cadastrar a mensagem
                        val textToSend = mensagemTexto.trim()
                        if (textToSend.isNotEmpty()) {
                            // opcional: adicionar mensagem localmente para mostrar imediatamente
                            val localMessage = MessageItem(
                                idMensagem = -1,
                                conteudo = textToSend,
                                idChat = chatId.toIntOrNull() ?: 0,
                                idUser = medicoUserId.toIntOrNull() ?: -1,
                                nomeUser = "Você",
                                createdAt = nowFormatted() // usa a data/hora atual formatada
                            )
                            mensagens = mensagens + localMessage
                            mensagemTexto = ""

                            // rolar imediatamente para ver a mensagem enviada
                            coroutineScope.launch {
                                listState.animateScrollToItem(mensagens.lastIndex)
                            }

                            // Chamada para a API enviar a mensagem
                            val idUser = SessionManager.getUserId(context)
                            val idInt = chatId.toIntOrNull() ?: 0
                            if (idInt > 0) {
                                // Criar objeto da mensagem para enviar
                                val mensagemParaEnviar = DadosMensagem(
                                    conteudo = textToSend,
                                    idChat = idInt,
                                    idUser = idUser
                                )

                                // Chamada otimista: tenta enviar a mensagem e atualiza a lista de mensagens
                                Conexao.getMessageService().cadastrarMensagem(mensagemParaEnviar).enqueue(object : Callback<ResponseMensagem> {
                                    override fun onResponse(call: Call<ResponseMensagem>, response: Response<ResponseMensagem>) {
                                        if (response.isSuccessful) {
                                            // Mensagem enviada com sucesso, podemos atualizar a lista se necessário
                                            // No momento, já estamos atualizando otimisticamente, então talvez não seja necessário refazer o fetch
                                            // fetchOnce(idInt) { newMessages -> mensagens = newMessages }
                                        } else {
                                            Log.e("CHAT_SCREEN", "Erro ao enviar mensagem: ${response.code()} - ${response.message()}")
                                            // Em caso de erro, podemos tentar um fetch para atualizar o estado
                                            fetchOnce(idInt) { newMessages -> mensagens = newMessages }
                                        }
                                    }

                                    override fun onFailure(call: Call<ResponseMensagem>, t: Throwable) {
                                        Log.e("CHAT_SCREEN", "Falha na chamada para enviar mensagem: ${t.message}")
                                        // Em caso de falha, tentamos um fetch para garantir que a lista de mensagens está atualizada
                                        fetchOnce(idInt) { newMessages -> mensagens = newMessages }
                                    }
                                })
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = Color(0xFF7986CB),
                    contentColor = Color.White
                ) {
                    Icon(
                        if (mensagemTexto.isBlank()) Icons.Default.Mic else Icons.Default.Send,
                        contentDescription = if (mensagemTexto.isBlank()) "Microfone" else "Enviar",
                        modifier = Modifier.size(20.dp)
                    )

                }
            }
        }
    }
}


@Composable
fun MessageRow(msg: MessageItem) {
    val context = LocalContext.current
    val currentUserId = SessionManager.getUserId(context)

    if (msg.idUser == currentUserId) {
        // Mensagem enviada pelo usuário - alinhar à direita
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Card(
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 4.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1E8D6)),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = msg.conteudo ?: "", color = Color.Black)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = formatIsoToDisplay(msg.createdAt), fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    } else {
        // Mensagem recebida - alinhar à esquerda
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = msg.nomeUser ?: "", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = msg.conteudo ?: "")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = formatIsoToDisplay(msg.createdAt), fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatIndividualScreenPreview() {
    ChatIndividualScreen(
        navegacao = null,
        contatoId = "1",
        contatoNome = "Ana Silva - mãe do João",
        medicoId = "2",
        medicoUserId = "3",
        chatId = "13"
    )
}
