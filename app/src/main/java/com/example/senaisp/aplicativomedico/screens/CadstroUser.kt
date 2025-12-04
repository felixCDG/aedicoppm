package com.example.senaisp.aplicativomedico.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.senaisp.aplicativomedico.R
import com.example.senaisp.aplicativomedico.model.CadastroUser
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.service.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.await

@Composable
fun CadastroUser(navegacao: NavHostController?) {
    var nameState = remember { mutableStateOf("") }
    var emailState = remember { mutableStateOf("") }
    var senhaState = remember { mutableStateOf("") }
    var CsenhaState = remember { mutableStateOf("") }
    var passwordVisible = remember { mutableStateOf(false) }
    var confirmPasswordVisible = remember { mutableStateOf(false) }
    var emailError = remember { mutableStateOf("") }
    var showEmailError = remember { mutableStateOf(false) }
    var passwordError = remember { mutableStateOf("") }
    var showPasswordError = remember { mutableStateOf(false) }
    
    val clienteApi = Conexao.getCadastroService()
    val context = LocalContext.current
    
    // Função para capitalizar nome (primeira letra de cada palavra)
    fun capitalizeName(name: String): String {
        return name.split(" ")
            .joinToString(" ") { word ->
                if (word.isNotEmpty()) {
                    word.lowercase().replaceFirstChar { it.uppercase() }
                } else {
                    word
                }
            }
    }
    
    // Função para validar email
    fun validateEmail(email: String): String {
        return when {
            email.length < 10 -> "Email deve ter pelo menos 10 caracteres"
            !email.endsWith("@gmail.com") -> "Email deve terminar com @gmail.com"
            else -> ""
        }
    }
    
    // Função para validar se as senhas coincidem
    fun validatePasswordMatch(password: String, confirmPassword: String): String {
        return if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            "As senhas não coincidem"
        } else {
            ""
        }
    }

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Lado esquerdo - Seção azul "Já possui conta?"
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
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
                    
                    // Linha até o meio com curva
                    lineTo(width - curveDepth, height * 0.3f)
                    
                    // Curva suave no meio
                    quadraticBezierTo(
                        width - curveDepth - 40f, height * 0.5f,
                        width - curveDepth, height * 0.7f
                    )
                    
                    // Linha até o canto inferior direito
                    lineTo(width, height)
                    
                    // Fechar o caminho pelos cantos esquerdos
                    lineTo(0f, height)
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
                    .padding(end = 37.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Já possui\nconta?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Para se manter\nconectado com a\ngente faça login",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Botão Conecte-se
                OutlinedButton(
                    onClick = {
                        navegacao?.navigate("login")
                    },
                    border = BorderStroke(2.dp, Color.White),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Conecte-se",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        // Lado direito - Formulário de cadastro
        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxSize()
                .padding(end = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Título "CADASTRE-SE"
            Text(
                text = "CADASTRE-\nSE",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = 38.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Botão "Continue com o Google"

            // Campo de Nome
            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = capitalizeName(it) },
                placeholder = { Text("NOME COMPLETO", color = Color.Gray, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Nome",
                        tint = Color(0xff081C60)
                    )
                },
                singleLine = true,
                maxLines = 1,
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
                singleLine = true,
                maxLines = 1,
                isError = showEmailError.value,
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
                onValueChange = { 
                    senhaState.value = it
                    // Revalidar confirmação de senha se já foi digitada
                    if (CsenhaState.value.isNotEmpty()) {
                        val error = validatePasswordMatch(it, CsenhaState.value)
                        passwordError.value = error
                        showPasswordError.value = error.isNotEmpty()
                    }
                },
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
            
            // Campo de Confirmar Senha
            OutlinedTextField(
                value = CsenhaState.value,
                onValueChange = { 
                    CsenhaState.value = it
                    val error = validatePasswordMatch(senhaState.value, it)
                    passwordError.value = error
                    showPasswordError.value = error.isNotEmpty()
                },
                placeholder = { Text("CONFIRMAR SENHA", color = Color.Gray, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Confirmar Senha",
                        tint = Color(0xff081C60)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible.value = !confirmPasswordVisible.value }) {
                        Icon(
                            if (confirmPasswordVisible.value) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible.value) "Ocultar senha" else "Mostrar senha",
                            tint = Color(0xff081C60)
                        )
                    }
                },
                isError = showPasswordError.value,
                singleLine = true,
                maxLines = 1,
                visualTransformation = if (confirmPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (showPasswordError.value) Color.Red else Color.Gray,
                    unfocusedBorderColor = if (showPasswordError.value) Color.Red else Color.Gray,
                    errorBorderColor = Color.Red
                )
            )
            
            // Mostrar erro da senha se houver
            if (showPasswordError.value) {
                Text(
                    text = passwordError.value,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            // Botão CADASTRAR
            Button(
                onClick = {
                    // Validar email antes de enviar
                    val emailValidation = validateEmail(emailState.value)
                    if (emailValidation.isNotEmpty()) {
                        emailError.value = emailValidation
                        showEmailError.value = true
                        return@Button
                    }
                    
                    // Validar se as senhas coincidem
                    val passwordValidation = validatePasswordMatch(senhaState.value, CsenhaState.value)
                    if (passwordValidation.isNotEmpty()) {
                        passwordError.value = passwordValidation
                        showPasswordError.value = true
                        return@Button
                    }
                    
                    val cliente = CadastroUser(
                        id_user = 0,
                        nome_user = nameState.value,
                        email = emailState.value,
                        senha = senhaState.value,
                        id_tipo = 4,
                    )

                    Log.i("Cadastro", " Enviando dados para API: $cliente")

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val response = clienteApi.cadastrarUsuario(cliente).await()

                            Log.i("API_CADASTRO", "Resposta completa: $response")
                            Log.i("API_CADASTRO", "Mensagem: ${response.message}")
                            Log.i("API_CADASTRO", "ID do usuário: ${response.data.id_user}")

                            SessionManager.saveUserId(context = context, userId = response.data.id_user)

                        } catch (e: Exception) {
                            Log.e("API_CADASTRO", "Erro ao cadastrar: ${e.message}")
                        }
                    }
                    navegacao?.navigate("login")
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF708EF1)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Cadastrar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CadastrosUsercreenPreview() {
    CadastroUser(navegacao = null)
}
