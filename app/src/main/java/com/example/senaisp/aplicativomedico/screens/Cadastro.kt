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
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.text.style.TextAlign
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

    val clienteApi = Conexao.getCadastroMedicoService()

    // --- helper formatting functions (string-based) ---
    fun formatarNome(text: String): String {
        return text.split(" ").joinToString(" ") { palavra ->
            if (palavra.isNotEmpty()) palavra.lowercase().replaceFirstChar { it.uppercase() } else palavra
        }
    }

    fun formatarData(text: String): String {
        val digitos = text.filter { it.isDigit() }
        return when {
            digitos.length <= 2 -> digitos
            digitos.length <= 4 -> "${digitos.substring(0, 2)}/${digitos.substring(2)}"
            digitos.length <= 8 -> "${digitos.substring(0, 2)}/${digitos.substring(2, 4)}/${digitos.substring(4)}"
            else -> "${digitos.substring(0, 2)}/${digitos.substring(2, 4)}/${digitos.substring(4, 8)}"
        }
    }

    fun converterDataParaBanco(dataFormatada: String): String {
        if (dataFormatada.length == 10) {
            val partes = dataFormatada.split("/")
            if (partes.size == 3) {
                return "${partes[2]}/${partes[1]}/${partes[0]}"
            }
        }
        return dataFormatada
    }

    fun formatarCPF(text: String): String {
        val digitos = text.filter { it.isDigit() }
        return when {
            digitos.length <= 3 -> digitos
            digitos.length <= 6 -> "${digitos.substring(0, 3)}.${digitos.substring(3)}"
            digitos.length <= 9 -> "${digitos.substring(0, 3)}.${digitos.substring(3, 6)}.${digitos.substring(6)}"
            digitos.length <= 11 -> "${digitos.substring(0, 3)}.${digitos.substring(3, 6)}.${digitos.substring(6, 9)}-${digitos.substring(9)}"
            else -> "${digitos.substring(0, 3)}.${digitos.substring(3, 6)}.${digitos.substring(6, 9)}-${digitos.substring(9, 11)}"
        }
    }

    fun formatarTelefone(text: String): String {
        val digitos = text.filter { it.isDigit() }
        return when {
            digitos.length <= 2 -> if (digitos.isNotEmpty()) "($digitos" else ""
            digitos.length <= 7 -> "(${digitos.substring(0, 2)}) ${digitos.substring(2)}"
            digitos.length <= 11 -> "(${digitos.substring(0, 2)}) ${digitos.substring(2, 7)}-${digitos.substring(7)}"
            else -> "(${digitos.substring(0, 2)}) ${digitos.substring(2, 7)}-${digitos.substring(7, 11)}"
        }
    }

    fun formatarCEP(text: String): String {
        val digitos = text.filter { it.isDigit() }
        return when {
            digitos.length <= 5 -> digitos
            digitos.length <= 8 -> "${digitos.substring(0, 5)}-${digitos.substring(5)}"
            else -> "${digitos.substring(0, 5)}-${digitos.substring(5, 8)}"
        }
    }


    Row(modifier = Modifier.fillMaxSize()) {
        // Left gradient panel
        Box(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFAEDCFF),
                            Color(0xFF2C91DE)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cadastre-se\nMédico",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navegacao?.navigate("login") },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Voltar para o Início",
                        color = Color.White,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Right form panel
        Column(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = "Médico",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cadastro Médico",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFF2C91DE))) {}

            Spacer(modifier = Modifier.height(24.dp))

            // Nome
            Text(text = "Nome completo *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nomeState.value,
                onValueChange = { nomeState.value = formatarNome(it) },
                placeholder = { Text("Digite o nome completo", fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Text(text = "Email", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                placeholder = { Text("exemplo@email.com", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CRM
            Text(text = "CRM", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = CRM.value,
                onValueChange = { CRM.value = it },
                placeholder = { Text("Digite seu CRM", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "CRM", tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CPF
            Text(text = "CPF", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = cpfState.value,
                onValueChange = { cpfState.value = formatarCPF(it) },
                placeholder = { Text("000.000.000-00", fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Telefone
            Text(text = "Telefone", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = telefoneState.value,
                onValueChange = { telefoneState.value = formatarTelefone(it) },
                placeholder = { Text("(00) 00000-0000", fontSize = 14.sp, color = Color.Gray) },
                leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = "Telefone", tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Foto upload area
            Text(text = "Upload de arquivos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clickable { pickImageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Imagem Selecionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Upload", modifier = Modifier.size(40.dp), tint = Color(0xFF2C91DE))
                        Text(text = "Clique para fazer upload", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sexo dropdown
            OutlinedTextField(
                value = selectedOption.value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(25.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                ),
                trailingIcon = {
                    IconButton(onClick = { expandedMenu.value = !expandedMenu.value }) {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Abrir menu", tint = Color.Gray)
                    }
                },
                placeholder = { Text("Selecione o sexo", fontSize = 14.sp, color = Color.Gray) }
            )

            DropdownMenu(expanded = expandedMenu.value, onDismissRequest = { expandedMenu.value = false }, modifier = Modifier.background(Color.White)) {
                DropdownMenuItem(text = { Text("Masculino") }, onClick = {
                    selectedOption.value = "Masculino"
                    selectedOptionId.value = 1
                    expandedMenu.value = false
                })
                DropdownMenuItem(text = { Text("Feminino") }, onClick = {
                    selectedOption.value = "Feminino"
                    selectedOptionId.value = 2
                    expandedMenu.value = false
                })
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (imageUri != null) {
                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val urlRetornada = uploadImageToAzure(context, imageUri!!)

                                withContext(Dispatchers.Main) {
                                    pictureState.value = urlRetornada
                                }

                                val idUser = SessionManager.getUserId(context)

                                val cliente = RegistroMedico(
                                    id_medico = 0,
                                    nome = nomeState.value,
                                    email = emailState.value,
                                    cpf = cpfState.value,
                                    telefone = telefoneState.value,
                                    foto = urlRetornada,
                                    crm = CRM.value,
                                    idSexo = selectedOptionId.value ?: 0,
                                    id_user = idUser
                                )

                                val gson = com.google.gson.GsonBuilder().setPrettyPrinting().create()
                                val jsonEnviado = gson.toJson(cliente)
                                Log.i("API_CADASTRO", "📦 JSON ENVIADO PARA API:\n$jsonEnviado")

                                val response = clienteApi.cadastrarResponsavel(cliente).await()
                                Log.i("API_CADASTRO", "Resposta: $response")

                                SessionManager.saveUserId(context = context, userId = response.data.id_user)
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
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "CADASTRAR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

        }
    }
}

@Preview
@Composable
private fun CadastroscreenPreview() {
    Cadastroscreen(navegacao = null)
}