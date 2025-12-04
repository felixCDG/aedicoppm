package com.example.senaisp.aplicativomedico.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.senaisp.aplicativomedico.repository.ContatoRepository
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.model.DadosChat
import com.example.senaisp.aplicativomedico.model.ResponseChat
import com.example.senaisp.aplicativomedico.service.SessionManager
import com.example.senaisp.aplicativomedico.ui.viewmodel.ContatoViewModel
import com.example.senaisp.aplicativomedico.ui.viewmodel.UiState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun ContatosScreen(
    navController: NavHostController?
) {
    val context = LocalContext.current

    // filtro digitado pelo usuário
    val filtroState = remember { mutableStateOf("") }

    // instancia o repository e viewmodel simples (sem DI)
    val repository = remember {
        ContatoRepository(
            Conexao.getBuscarContatoService(),
            bearerProvider = { SessionManager.getBearerToken(context) }
        )
    }
    val viewModel = remember { ContatoViewModel(repository) }

    // manter texto sincronizado com viewModel
    LaunchedEffect(filtroState.value) {
        viewModel.setQuery(filtroState.value)
    }

    val uiState by viewModel.uiState().collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF7986CB))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController?.popBackStack() }
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Encontre seus contatos",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Conteúdo principal com fundo branco
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de busca
            OutlinedTextField(
                value = filtroState.value,
                onValueChange = { filtroState.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7986CB),
                    unfocusedBorderColor = Color(0xFF7986CB),
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { /* Ação opcional de busca */ }
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Buscar",
                            tint = Color(0xFF7986CB)
                        )
                    }
                },
                placeholder = {
                    Text(
                        "Buscar contatos...",
                        color = Color.Gray
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Conteúdo dinâmico de acordo com o estado
            when (uiState) {
                is UiState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "Digite para buscar contatos",
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                }
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    val items = (uiState as UiState.Success).items
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Text(
                                text = "Nenhum resultado",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(items) { contato ->
                                ContatoItem(
                                    nome = contato.nomeResponsavel ?: contato.nomeUser ?: "",
                                    tipo = contato.cpf ?: contato.telefone ?: "Contato",
                                    onClick = {
                                        // Salva o ID do usuário exibido no card
                                        // Se houver um idUser, consideramos que o nome exibido é do usuário
                                        val contatoId = contato.idUser?.toString() ?: contato.idResponsavel?.toString() ?: ""
                                        val contatoNome = contato.nomeResponsavel ?: contato.nomeUser ?: ""

                                        // Não sobrescrever o userId salvo no login com o id do contato.
                                        // Apenas salvamos o id do responsável (se existir) em seu campo específico.
                                        if (contato.idResponsavel != null) {
                                            SessionManager.saveResponsavelId(context, contato.idResponsavel)
                                            Log.d("CONTATOS_SCREEN", "Salvou responsavelId: ${contato.idResponsavel}")
                                            val storedRespId = SessionManager.getResponsavelId(context)
                                            Log.d("CONTATOS_SCREEN", "ResponsavelId armazenado (getResponsavelId): $storedRespId")
                                        }

                                        // Recupera o id do médico (se houver) e registra no log e salva (não sobrescreve se -1)
                                        val medicoId = SessionManager.getMedicoId(context)
                                        Log.d("CONTATOS_SCREEN", "MedicoId atual (SessionManager.getMedicoId): $medicoId")

                                        // Recupera o idUser salvo no login e usaremos esse id como user1 ao criar o chat
                                        val userId = SessionManager.getUserId(context)
                                        Log.d("CONTATOS_SCREEN", "UserId atual (SessionManager.getUserId): $userId")

                                        // Antes usávamos getMedicoUserId; agora enviamos o idUser salvo no login
                                        // val medicoUserId = SessionManager.getMedicoUserId(context)
                                        // Log.d("CONTATOS_SCREEN", "MedicoUserId atual (SessionManager.getMedicoUserId): $medicoUserId")

                                        // Navega para o chat individual passando id do contato, nome e o medicoId + userId (nome deve ser codificado)
                                        if (contatoId.isNotBlank()) {
                                            // Build DadosChat: userId (do login) será user1, contato user id é user2
                                            val userIdInt = userId
                                            val contatoUserIdInt = contato.idUser ?: contato.idResponsavel ?: -1

                                            if (userIdInt == -1) {
                                                Log.w("CONTATOS_SCREEN", "UserId (salvo no login) não disponível. Continuando sem criação de chat.")
                                                val encodedName = Uri.encode(contatoNome)
                                                navController?.navigate("chatin/$contatoId/$encodedName/$medicoId/$userId/0")
                                                return@ContatoItem
                                            }

                                            val dadosChat = DadosChat(
                                                userId1 = userIdInt,
                                                userId2 = contatoUserIdInt
                                            )

                                            // Chama o serviço de cadastro de chat
                                            val cadastroService = Conexao.getCadastrarChatService()
                                            cadastroService.cadastrarChat(dadosChat).enqueue(object : Callback<ResponseChat> {
                                                override fun onResponse(call: Call<ResponseChat>, response: Response<ResponseChat>) {
                                                    if (response.isSuccessful) {
                                                        val body = response.body()
                                                        if (body != null) {
                                                            Log.d("CONTATOS_SCREEN", "Resposta cadastro chat: ${body}")
                                                            if (body.status_code == 201) {
                                                                val chatId = body.chat.id_chat
                                                                // Agora busca as mensagens do chat criado
                                                                val chatMessageService = Conexao.getChatMessageService()
                                                                chatMessageService.getMessagesByChatId(chatId).enqueue(object : Callback<com.example.senaisp.aplicativomedico.model.ResponseMensagens> {
                                                                    override fun onResponse(call: Call<com.example.senaisp.aplicativomedico.model.ResponseMensagens>, resp: Response<com.example.senaisp.aplicativomedico.model.ResponseMensagens>) {
                                                                        if (resp.isSuccessful) {
                                                                            val respBody = resp.body()
                                                                            if (respBody != null) {
                                                                                if (respBody.status_code == 200) {
                                                                                    val messages = respBody.mensagens
                                                                                    Log.d("CONTATOS_SCREEN", "Mensagens obtidas para chat $chatId: $messages")
                                                                                } else {
                                                                                    Log.w("CONTATOS_SCREEN", "GET mensagens retornou status_code != 200: ${respBody.status_code}")
                                                                                }
                                                                            } else {
                                                                                Log.w("CONTATOS_SCREEN", "GET mensagens: corpo da resposta é nulo")
                                                                            }
                                                                        } else {
                                                                            Log.w("CONTATOS_SCREEN", "Falha ao obter mensagens: ${resp.code()} - ${resp.message()}")
                                                                        }
                                                                        // Após tentar obter mensagens (sucesso ou falha), navegamos para a tela de chat
                                                                        val encodedName = Uri.encode(contatoNome)
                                                                        navController?.navigate("chatin/$contatoId/$encodedName/$medicoId/$userId/$chatId")
                                                                    }

                                                                    override fun onFailure(call: Call<com.example.senaisp.aplicativomedico.model.ResponseMensagens>, t: Throwable) {
                                                                        Log.e("CONTATOS_SCREEN", "Erro na chamada de mensagens: ${t.message}")
                                                                        val encodedName = Uri.encode(contatoNome)
                                                                        navController?.navigate("chatin/$contatoId/$encodedName/$medicoId/$userId/$chatId")
                                                                    }
                                                                })
                                                            } else {
                                                                Log.w("CONTATOS_SCREEN", "Cadastro de chat retornou status_code != 201: ${body.status_code}")
                                                                val encodedName = Uri.encode(contatoNome)
                                                                // sem chatId disponível, navegamos com chatId = 0
                                                                navController?.navigate("chatin/$contatoId/$encodedName/$medicoId/$userId/0")
                                                            }
                                                        } else {
                                                            Log.e("CONTATOS_SCREEN", "Resposta vazia ao cadastrar chat")
                                                            val encodedName = Uri.encode(contatoNome)
                                                            navController?.navigate("chatin/$contatoId/$encodedName/$medicoId/$userId/0")
                                                        }
                                                    } else {
                                                        Log.e("CONTATOS_SCREEN", "Falha na requisição de cadastro de chat: ${response.code()} - ${response.message()}")
                                                        val encodedName = Uri.encode(contatoNome)
                                                        navController?.navigate("chatin/$contatoId/$encodedName/$medicoId/$userId/0")
                                                    }
                                                }

                                                override fun onFailure(call: Call<ResponseChat>, t: Throwable) {
                                                    Log.e("CONTATOS_SCREEN", "Erro ao cadastrar chat: ${t.message}")
                                                    val encodedName = Uri.encode(contatoNome)
                                                    navController?.navigate("chatin/$contatoId/$encodedName/$medicoId/$userId/0")
                                                }
                                            })

                                        }

                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    val msg = (uiState as UiState.Error).message
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Erro: $msg", color = Color.Red)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ContatoItem(
    nome: String,
    tipo: String = "",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar com indicador de status
            Box(
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7986CB)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Indicador de status online/offline (pode ser adicionado aqui)

            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nome e tipo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nome,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )

                Text(
                    text = tipo,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ContatosScreenPreview() {
    ContatosScreen(navController = null)
}
