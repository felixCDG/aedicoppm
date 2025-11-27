package com.example.senaisp.aplicativomedico.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextField
import androidx.navigation.NavHostController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.service.ChamadaRequest
import com.example.senaisp.aplicativomedico.service.ChamadaService
import com.example.senaisp.aplicativomedico.service.SessionManager
import okhttp3.ResponseBody
import retrofit2.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import android.content.Context

@Composable
fun SalaConsulta(navegacao: NavHostController?) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo SOS BABY
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = com.example.senaisp.aplicativomedico.R.drawable.logocerta),
                contentDescription = "Bebê",
                modifier = Modifier.height(42.dp)
            )
        }

        // Navegação central
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Home",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.clickable {
                    navegacao?.navigate("home")// Já estamos na Home, apenas scroll para o topo se necessário
                }
            )
            Text(
                text = "Chat",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.clickable {
                    navegacao?.navigate("contatos")
                }
            )
            Text(
                text = "BabyIA",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.clickable {
                    navegacao?.navigate("babyia")
                }
            )
        }

        // Ações do lado direito
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ícone de notificação
            IconButton(
                onClick = {
                    // navController?.navigate("notificacoes")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF7986CB),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7986CB))
                    .clickable {
                        // navController?.navigate("perfil")
                    },
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        navegacao?.navigate("perfilresp")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun CreateCallScreen(navegacao: NavHostController?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var roomName by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Reuse the existing top header
        SalaConsulta(navegacao)

        // Content: centered card with title, single-line input and primary button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(0.9f)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Criar Chamada",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = roomName,
                        onValueChange = { if (it.length <= 100) roomName = it },
                        placeholder = { Text("Nome da sala") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            // Basic validation
                            error = ""
                            if (roomName.isBlank()) {
                                error = "Nome da sala obrigatório"
                                return@Button
                            }

                            // Trigger network call
                            loading = true
                            scope.launch(Dispatchers.IO) {
                                // Read token from SessionManager
                                val bearer = SessionManager.getBearerToken(context)

                                val chamadaService = Conexao().getChamadaService()
                                val requestBody = ChamadaRequest(roomName)
                                try {
                                    val response: Response<ResponseBody> = chamadaService.criarChamada(bearer, requestBody).execute()

                                    if (response.isSuccessful) {
                                        withContext(Dispatchers.Main) {
                                            navegacao?.navigate("video/${URLEncoder.encode(roomName, "utf-8")}")
                                        }
                                    } else {
                                        val errorText = response.errorBody()?.string()
                                        withContext(Dispatchers.Main) {
                                            error = "Erro ao criar chamada. (Código ${response.code()})"
                                            if (!errorText.isNullOrBlank()) {
                                                error += " - ${errorText.take(200)}"
                                            }
                                        }
                                    }
                                } catch (_: Exception) {
                                    withContext(Dispatchers.Main) {
                                        error = "Erro ao conectar ao servidor."
                                    }
                                } finally {
                                    withContext(Dispatchers.Main) {
                                        loading = false
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F6BE4)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading
                    ) {
                        Text(text = if (loading) "Criando..." else "Criar Chamada", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (error.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error, color = Color.Red)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateCallScreenPreview() {
    CreateCallScreen(navegacao = null)
}

@Preview
@Composable
private fun SalaConsultaPreview() {
    SalaConsulta(navegacao = null)
}