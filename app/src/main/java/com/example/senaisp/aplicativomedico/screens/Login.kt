package com.example.senaisp.aplicativomedico.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.senaisp.aplicativomedico.R
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import com.example.senaisp.aplicativomedico.model.Login
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.service.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Loginscreen(navegacao: NavHostController?) {
    var emailState = remember { mutableStateOf("") }
    var senhaState = remember { mutableStateOf("") }
    var passwordVisible = remember { mutableStateOf(false) }
    var emailError = remember { mutableStateOf("") }
    var showEmailError = remember { mutableStateOf(false) }
    
    val clienteApi = Conexao.getLoginService()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Função para validar email
    fun validateEmail(email: String): String {
        return when {
            !email.endsWith("@gmail.com") -> "Email deve terminar com @gmail.com"
            else -> ""
        }
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Lado esquerdo - Formulário de login
        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxSize()
                .padding(start = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Título "Entrar com"
            Text(
                text = "Entrar com",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 38.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Botão "Continue com o Google"
            OutlinedButton(
                onClick = { /* Implementar login com Google */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Gray),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_google), // Você precisará adicionar este ícone
                    contentDescription = "Google",
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue com o Google",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Texto "Ou use seu email para login"
            Text(
                text = "Ou use seu email para login",
                fontSize = 12.sp,
                color = Color(0xff081C60),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Campo de Email
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { 
                    emailState.value = it
                    val error = validateEmail(it)
                    emailError.value = error
                    showEmailError.value = error.isNotEmpty()
                },
                placeholder = { Text("EMAIL", color = Color.Gray, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xff081C60)
                    )
                },
                isError = showEmailError.value,
                singleLine = true,
                maxLines = 1,
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (showEmailError.value) Color.Red else Color.Gray,
                    unfocusedBorderColor = if (showEmailError.value) Color.Red else Color.Gray,
                    errorBorderColor = Color.Red
                )
            )
            
            // Mostrar erro do email se houver
            if (showEmailError.value) {
                Text(
                    text = emailError.value,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo de Senha
            OutlinedTextField(
                value = senhaState.value,
                onValueChange = { senhaState.value = it },
                placeholder = { Text("SENHA", color = Color.Gray, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Senha",
                        tint = Color(0xff081C60)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                        Icon(
                            if (passwordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible.value) "Ocultar senha" else "Mostrar senha",
                            tint = Color(0xff081C60)
                        )
                    }
                },
                singleLine = true,
                maxLines = 1,
                visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Link "Esqueceu sua senha?"
            ClickableText(
                style = TextStyle(
                    color = Color(0xff081C60),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
                text = buildAnnotatedString {
                    pushStringAnnotation(tag = "esqueceu", annotation = "esqueceu")
                    withStyle(style = SpanStyle(color = Color(0xFF7986CB))) {
                        append("Esqueceu sua senha?")
                    }
                    pop()
                },
                onClick = { offset ->
                    // Implementar navegação para recuperação de senha
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Botão LOGAR
            Button(
                onClick = {
                    // Validar email antes de enviar
                    val emailValidation = validateEmail(emailState.value)
                    if (emailValidation.isNotEmpty()) {
                        emailError.value = emailValidation
                        showEmailError.value = true
                        return@Button
                    }
                    
                    val cliente = Login(
                        email = emailState.value,
                        senha = senhaState.value,
                    )

                    val json = Gson().toJson(cliente)
                    Log.i("Login JSON", json)

                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val response = clienteApi.loginUsuario(cliente).execute()
                            
                            if (response.isSuccessful && response.body() != null) {
                                val loginUsuario = response.body()!!
                                Log.i("API_LOGIN", "Resposta completa: $loginUsuario")

                                SessionManager.saveUserId(context, loginUsuario.data.id_user)
                                SessionManager.saveAuthToken(context, loginUsuario.token)

                                withContext(Dispatchers.Main) {
                                    navegacao?.navigate("home")
                                }
                            } else {
                                Log.e("API_LOGIN", "Erro na resposta: ${response.errorBody()?.string()}")
                            }

                        } catch (e: Exception) {
                            Log.e("API_LOGIN", "Erro ao Logar: ${e.message}")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF708EF1)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "LOGAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
        
        // Lado direito - Seção azul de boas-vindas com curvatura
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxSize()
        ) {
            // Canvas para desenhar a forma curva
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val path = Path().apply {
                    val width = size.width
                    val height = size.height
                    val curveDepth = 100f
                    
                    // Começar do canto superior esquerdo (topo da tela)
                    moveTo(0f, 0f)
                    
                    // Linha até o topo direito
                    lineTo(width, 0f)
                    
                    // Linha até o fundo direito
                    lineTo(width, height)
                    
                    // Linha até o fundo esquerdo
                    lineTo(0f, height)
                    
                    // Linha até o meio com curva (subindo)
                    lineTo(curveDepth, height * 0.7f)
                    
                    // Curva suave no meio
                    quadraticBezierTo(
                        curveDepth + 40f, height * 0.5f,
                        curveDepth, height * 0.3f
                    )
                    
                    // Linha até o topo esquerdo
                    lineTo(0f, 0f)
                    
                    close()
                }
                
                drawPath(
                    path = path,
                    color = Color(0xFF708EF1)
                )
            }
            
            // Conteúdo da coluna
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 37.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text(
                text = "Olá Usuário !!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Cadastre-se e\ncomeçe a usar\nnossa plataforma",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Botão Cadastre-se
            OutlinedButton(
                onClick = {
                    navegacao?.navigate("cadastro")
                },
                border = BorderStroke(2.dp, Color.White),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .width(130.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Cadastre-se",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginscreenPreview() {
    Loginscreen(navegacao = null)
}
