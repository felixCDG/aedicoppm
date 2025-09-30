package com.example.senaisp.aplicativomedico.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.senaisp.aplicativomedico.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await

@Composable
fun Loginscreen(navegacao: NavHostController?) {

    var emailState = remember {
        mutableStateOf("")
    }
    var senhaState = remember {
        mutableStateOf("")
    }


    Box(modifier = Modifier.fillMaxSize().background(color = Color(0xFFAEDCFF))) {
        Column (
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(R.drawable.logoagoravai),
                contentDescription = "",
                modifier = Modifier
                    .size(280.dp),

                )
        }
        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(570.dp),
                shape = CurvedTopShape(),// aplica o shape
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF)
                )
            ) {
                Column (
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                ){
                    Text(modifier = Modifier .padding(top = 120.dp),
                        text = "LOGIN",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 44.sp
                    )
                    Spacer(modifier = Modifier .height(24.dp))
                    Text(
                        text = "Email",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Spacer( modifier = Modifier .height(5.dp))
                    OutlinedTextField(
                        value = emailState.value,
                        onValueChange = {
                            emailState.value = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp, // 👈 tamanho da borda
                                color = Color(0xFF2C91DE),
                                shape = RoundedCornerShape(30.dp)
                            ),
                        shape = RoundedCornerShape(30.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0x65AEDCFF),
                            unfocusedContainerColor = Color(0x65AEDCFF)
                        ),
                    )
                    Spacer(modifier = Modifier .height(24.dp))
                    Text(
                        text = "Senha",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Spacer( modifier = Modifier .height(5.dp))
                    OutlinedTextField(
                        value = senhaState.value,
                        onValueChange = {
                            senhaState.value = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp, // 👈 tamanho da borda
                                color = Color(0xFF2C91DE),
                                shape = RoundedCornerShape(30.dp)
                            ),
                        shape = RoundedCornerShape(30.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0x65AEDCFF),
                            unfocusedContainerColor = Color(0x65AEDCFF)
                        ),
                    )
                    Spacer( modifier = Modifier .height(34.dp))
                    Column (
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Button(
                            onClick = {
                                /*val cliente = Login(
                                    email = emailState.value,
                                    senha = senhaState.value,
                                )

                                val json = com.google.gson.Gson().toJson(cliente)
                                Log.i("Login JSON", json)

                                GlobalScope.launch(Dispatchers.IO) {
                                    try {
                                        val loginUsuario = clienteApi.loginUsuario(cliente).await()

                                        // Mensagem de sucesso no Logcat
                                        Log.i("Login", "Login realizado com sucesso! Dados: $loginUsuario")

                                        withContext(Dispatchers.Main) {
                                            navegacao?.navigate("cadastroR")
                                        }

                                    } catch (e: Exception) {
                                        // Mensagem de erro no Logcat
                                        Log.e("Login", "Erro ao Logar: ${e.message}")
                                    }
                                }*/

                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFFAEDCFF)),
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .width(270.dp)
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF2C91DE),
                                    shape = RoundedCornerShape(38.dp)
                                ),
                        ) {
                            Text(
                                text = "ENTRAR",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer( modifier = Modifier .height(3.dp))
                        val textoClique = buildAnnotatedString {
                            append("Não tem conta ?")

                            // Parte clicável: "Cadastre-se"
                            pushStringAnnotation(tag = "Cadastre-se", annotation = "Cadastre-se")
                            withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.Black)) {
                                append("Cadastre-se")
                            }
                            pop()
                        }

                        ClickableText(
                            text = textoClique,
                            modifier = Modifier.padding(horizontal = 37.dp),
                            onClick = { offset ->
                                textoClique.getStringAnnotations(tag = "Cadastre-se", start = offset, end = offset)
                                    .firstOrNull()?.let {
                                        navegacao?.navigate("cadastro")
                                    }
                            }
                        )
                    }
                }
            }
        }
    }



}

@Preview
@Composable
private fun LoginscreenPreview() {
    Loginscreen(navegacao = null)
}