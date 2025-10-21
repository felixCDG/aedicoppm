package com.example.senaisp.aplicativomedico.screens

import android.R.id.input
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.senaisp.aplicativomedico.R
import com.example.senaisp.aplicativomedico.model.ResponsePerfilMedico
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.service.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.collections.firstOrNull
import kotlin.collections.isNullOrEmpty

@Composable
fun PerfilMedic(navegacao: NavHostController?) {


    val context = LocalContext.current

// Estados dos campos
    var nomeState = remember { mutableStateOf("") }
    var telefoneState = remember { mutableStateOf("") }
    var CRM = remember { mutableStateOf("") }
    var emailState = remember { mutableStateOf("") }

// Pegar o ID salvo localmente
    LaunchedEffect(Unit) {
        // ID fixo que você sabe que existe no banco
        val idMedico = SessionManager.getMedicoId(context)

        val call = Conexao()
            .getCadastroMedicoService()
            .getMedicoById(1)

        Log.i("API_REQUEST", "GET URL: ${call.request().url}")
        Log.i("API_REQUEST", "Método: ${call.request().method}")
        Log.i("API_REQUEST", "Cabeçalhos: ${call.request().headers}")

        call.enqueue(object : Callback<ResponsePerfilMedico> {
            override fun onResponse(
                call: Call<ResponsePerfilMedico>,
                response: Response<ResponsePerfilMedico>
            ) {
                if (response.isSuccessful) {
                    val respList = response.body()?.medico
                    if (!respList.isNullOrEmpty()) {
                        val respData = respList[0] // Pega o primeiro elemento
                        nomeState.value = respData.nome
                        CRM.value = respData.crm
                        telefoneState.value = respData.telefone
                        emailState.value = respData.usuario?.firstOrNull()?.email ?: "Não disponível"

                    } else {
                        nomeState.value = "Não encontrado"
                        telefoneState.value = "-"
                        CRM.value = "-"
                        emailState.value = "-"
                    }
                } else {
                    Log.e("PerfilMedic", "Erro HTTP: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponsePerfilMedico>, t: Throwable) {
                Log.e("PerfilMedic", "Erro ao buscar perfil: ${t.message}")
            }
        })
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFAEDCFF))) {

        Image(
            painter = painterResource(id = R.drawable.fundoback2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column (
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column (
                modifier = Modifier
                    .padding(13.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ){
                Row (
                    modifier = Modifier
                        .padding(bottom = 58.dp)
                        .fillMaxWidth(),
                ){
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                    )
                    Spacer(modifier = Modifier .width(5.dp))
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier
                            .size(40.dp)
                    )
                    Spacer(modifier = Modifier .width(5.dp))
                    Text(
                        modifier = Modifier
                            .padding(top = 7.dp),
                        text = "Perfil do Responsavel",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(715.dp),
                shape = RoundedCornerShape(
                    topStart = 33.dp,
                    topEnd =  33.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF)
                )
            ) {
                Column (
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            modifier = Modifier
                                .padding(top = 12.dp),
                            text = nomeState.value,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "",
                            modifier = Modifier
                                .size(125.dp)
                        )
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(25.dp)
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Nome",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        OutlinedTextField(
                            value = nomeState.value,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp, // 👈 tamanho da borda
                                    color = Color(0xFF2C91DE),
                                    shape = RoundedCornerShape(30.dp)
                                ),
                            shape = RoundedCornerShape(30.dp),
                            placeholder = {
                                Text("000.000.000-00")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "TELEFONE",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        OutlinedTextField(
                            value = telefoneState.value,
                            onValueChange = {
                                telefoneState.value = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp, // 👈 tamanho da borda
                                    color = Color(0xFF2C91DE),
                                    shape = RoundedCornerShape(30.dp)
                                ),
                            shape = RoundedCornerShape(30.dp),
                            placeholder = {
                                Text("(00) 00000-0000")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Email",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        OutlinedTextField(
                            value = emailState.value,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 2.dp, // 👈 tamanho da borda
                                    color = Color(0xFF2C91DE),
                                    shape = RoundedCornerShape(30.dp)
                                ),
                            shape = RoundedCornerShape(30.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "email"
                                )
                            },
                            placeholder = {
                                Text("email")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )
                        Spacer(modifier = Modifier.height(34.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.End
                        ) {

                            val context = LocalContext.current

                            Button(
                                onClick = {

                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFFAEDCFF)),
                                shape = RoundedCornerShape(30.dp),
                                modifier = Modifier
                                    .padding(bottom = 70.dp)
                                    .width(180.dp)
                                    .border(
                                        width = 2.dp, // 👈 tamanho da borda
                                        color = Color(0xFF2C91DE),
                                        shape = RoundedCornerShape(38.dp)
                                    ),
                            ) {
                                Row (
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ){
                                    Text(
                                        text = "FECHAR",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier .width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "",
                                        tint = Color(0xFF000000),
                                        modifier = Modifier
                                            .size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))

                        }
                    }
                }
            }
        }
    }



}

@Preview
@Composable
private fun PerfilMedicPreview() {
    PerfilMedic(navegacao = null)
}

