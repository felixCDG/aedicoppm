package com.example.senaisp.aplicativomedico.screens


import android.R.attr.id
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.senaisp.aplicativomedico.R
import com.example.senaisp.aplicativomedico.model.RegistroMedico
import com.example.senaisp.aplicativomedico.service.AzureUploadService.uploadImageToAzure
import com.example.senaisp.aplicativomedico.service.Conexao
import com.example.senaisp.aplicativomedico.service.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await


fun CurvedTopShape() = GenericShape { size, _ ->
    moveTo(0f, size.height) // canto inferior esquerdo
    lineTo(0f, size.height * 0.2f) // sobe até perto do topo

    // curva superior (você pode ajustar os pontos de controle)
    quadraticBezierTo(
        size.width * 0.25f, -8f,     // ponto de controle
        size.width * 0.5f, size.height * 0.1f // ponto final

    )
    quadraticBezierTo(
        size.width * 0.75f, size.height * 0.2f,
        size.width, 0f
    )

    lineTo(size.width, size.height) // desce lado direito
    close()
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cadastroscreen(navegacao: NavHostController?) {


    var expandedTipoId by remember { mutableStateOf(false) }
    var selectTipoId by remember { mutableStateOf("") }
    val selectedTipoIddrop = remember { mutableStateOf(0) }


    val expandedMenu = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf("") }


    // 1) Estado para armazenar o URI da imagem escolhida
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 2) Estado para armazenar a URL retornada pelo Azure
    var imageUrl by remember { mutableStateOf<String?>(null) }

    // 3) Launcher para pegar o arquivo via Galeria
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }

    val context = LocalContext.current

    var pictureState = remember {
        mutableStateOf("")
    }

    val selectedOptionId = remember { mutableStateOf<Int?>(null) }

    var nomeState = remember {
        mutableStateOf("")
    }
    var cpfState = remember {
        mutableStateOf("")
    }
    var telefoneState = remember {
        mutableStateOf("")
    }
    var emailState = remember {
        mutableStateOf("")
    }
    var CRM = remember {
        mutableStateOf("")
    }



    var mensagem by remember { mutableStateOf("") }
    var isErro by remember { mutableStateOf(false) }

    val clienteApi = Conexao().getCadastroMedicoService()


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFAEDCFF))) {

        Column(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 27.dp),
                text = "Cadastre-se",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 44.sp
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(823.dp),
                shape = CurvedTopShape(),// aplica o shape
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFFFFF)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                ) {
                    Spacer(modifier = Modifier.height(90.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalHospital,
                                contentDescription = "",
                                modifier = Modifier
                                    .size(400.dp)
                            )
                        }
                        Spacer(modifier = Modifier,)
                        Text(
                            modifier = Modifier
                                .padding(top = 12.dp),
                            text = "Medico",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        OutlinedTextField(
                            value = nomeState.value,
                            onValueChange = {
                                nomeState.value = it
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            label = {
                                Text("Nome")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C91DE),
                                unfocusedBorderColor = Color(0xFF2C91DE),
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = emailState.value,
                            onValueChange = {
                                emailState.value = it
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Senha"
                                )
                            },
                            label = {
                                Text("Email")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C91DE),
                                unfocusedBorderColor = Color(0xFF2C91DE),
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = CRM.value,
                            onValueChange = {
                                CRM.value = it
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            label = {
                                Text("CRM")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C91DE),
                                unfocusedBorderColor = Color(0xFF2C91DE),
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = cpfState.value,
                            onValueChange = {
                                cpfState.value = it
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            label = {
                                Text("CPF")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C91DE),
                                unfocusedBorderColor = Color(0xFF2C91DE),
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = telefoneState.value,
                            onValueChange = {
                                telefoneState.value = it
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Senha"
                                )
                            },
                            label = {
                                Text("Telefone")
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C91DE),
                                unfocusedBorderColor = Color(0xFF2C91DE),
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF)
                            ),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = pictureState.value,
                            onValueChange = { },
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C91DE),
                                unfocusedBorderColor = Color(0xFF2C91DE),
                                disabledBorderColor = Color(0xFF2C91DE), // 👈 garante igual
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF),
                                disabledContainerColor = Color(0x65AEDCFF) // 👈 garante igual
                            ),

                            label = {
                                Text(
                                    text = "Foto do Medico",
                                    fontSize = 16.sp,
                                    color = Color(0xFF000000)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "",
                                    tint = Color(0xFF000000),
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .size(40.dp)
                                )
                            },
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pickImageLauncher.launch("image/*")
                                }
                        )
                        imageUri?.let { uri ->
                            AsyncImage(
                                model = uri,
                                contentDescription = "Imagem Selecionada",
                                modifier = Modifier
                                    .padding(vertical = 10.dp, horizontal = 15.dp)
                                    .size(150.dp)
                                    .border(2.dp, Color(0x80241508))
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        OutlinedTextField(
                            value = selectedOption.value,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(30.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C91DE),
                                unfocusedBorderColor = Color(0xFF2C91DE),
                                disabledBorderColor = Color(0xFF2C91DE), // 👈 garante igual
                                focusedContainerColor = Color(0x65AEDCFF),
                                unfocusedContainerColor = Color(0x65AEDCFF),
                                disabledContainerColor = Color(0x65AEDCFF) // 👈 garante igual
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    expandedMenu.value = !expandedMenu.value
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Abrir menu"
                                    )
                                }
                            },
                            placeholder = {
                                Text("Selecione o sexo")
                            },
                        )
                        DropdownMenu(
                            expanded = expandedMenu.value,
                            onDismissRequest = { expandedMenu.value = false },
                            modifier = Modifier.background(Color(0xFFFFFFFF))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Masculino") },
                                onClick = {
                                    selectedOption.value = "Masculino"
                                    selectedOptionId.value = 1
                                    expandedMenu.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Feminino") },
                                onClick = {
                                    selectedOption.value = "Feminino"
                                    selectedOptionId.value = 2
                                    expandedMenu.value = false
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(44.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            val context = LocalContext.current

                            Button(
                                onClick = {
                                    if (imageUri != null) {
                                        GlobalScope.launch(Dispatchers.IO) {
                                            try {
                                                // 1️⃣ Faz o upload e pega a URL
                                                val urlRetornada = uploadImageToAzure(context, imageUri!!)

                                                withContext(Dispatchers.Main) {
                                                    pictureState.value = urlRetornada // atualiza o state
                                                }

                                                val idUser = SessionManager.getUserId(context)

                                                // 2️⃣ Cria o objeto com a URL correta
                                                val cliente = RegistroMedico(
                                                    id_medico = 0,
                                                    nome = nomeState.value,
                                                    email = emailState.value,
                                                    cpf = cpfState.value,
                                                    telefone = telefoneState.value,
                                                    foto = urlRetornada, // <-- usa a URL retornada
                                                    crm = CRM.value,
                                                    idSexo = selectedOptionId.value ?: 0,
                                                    id_user = 1
                                                )


                                                // 3️⃣ Loga o JSON que será enviado
                                                val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
                                                val jsonEnviado = gson.toJson(cliente)
                                                Log.i("API_CADASTRO", "📦 JSON ENVIADO PARA API:\n$jsonEnviado")


                                                val response = clienteApi.cadastrarResponsavel(cliente).await()
                                                Log.i("API_CADASTRO", "Resposta: $response")
                                                Log.i("API_CADASTRO", "Resposta completa: $response")
                                                Log.i("API_CADASTRO", "Mensagem: ${response.message}")
                                                Log.i("API_CADASTRO", "ID do usuário: ${response.data.id_user}")

                                                // Salva o ID para usar depois
                                                SessionManager.saveUserId(context = context, userId = response.data.id_user)
                                                // Salva o ID do responsável no SessionManager
                                                SessionManager.saveMedicoId(context, response.data.id_medico)

                                                withContext(Dispatchers.Main) {
                                                    navegacao?.navigate("perfil")
                                                }

                                            } catch (e: Exception) {
                                                Log.e("API_CADASTRO", "Erro: ${e.message}")
                                                withContext(Dispatchers.Main) {
                                                    navegacao?.navigate("perfil")
                                                }
                                            }
                                        }
                                    } else {
                                        Log.e("UPLOAD", "Nenhuma imagem selecionada")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(Color(0xFFAEDCFF)),
                                shape = RoundedCornerShape(30.dp),
                                modifier = Modifier
                                    .width(270.dp)
                                    .border(
                                        width = 2.dp, // 👈 tamanho da borda
                                        color = Color(0xFF2C91DE),
                                        shape = RoundedCornerShape(38.dp)
                                    ),
                            ) {
                                Text(
                                    text = "CADASTRAR",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
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
private fun CadastroscreenPreview() {
    Cadastroscreen(navegacao = null)
}