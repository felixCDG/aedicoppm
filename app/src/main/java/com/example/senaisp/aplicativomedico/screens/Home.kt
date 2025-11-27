package com.example.senaisp.aplicativomedico.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import java.time.LocalDate
import java.time.YearMonth
import kotlin.collections.forEach
import kotlin.text.take
import kotlin.to

@Composable
fun HomeScreen(navegacao: NavHostController?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF2FF)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            TopHeader(navegacao = navegacao)
            HeroSection()
            ServicesSection()
            CalendarSection(navController = navegacao)
            RoutineSection(navController = navegacao)
            HistorySection()
            ChatSection()
            FooterSection()
        }
    }
}

@Composable
fun TopHeader(navegacao: NavHostController?) {
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
                text = "Consulta",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.clickable {
                    navegacao?.navigate("create_call")// Já estamos na Home, apenas scroll para o topo se necessário
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
fun HeroSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF103D8C))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "CUIDAR É AMAR",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Descubra como nossos serviços ajudam você a acompanhar cada momento do seu bebê com mais segurança e carinho.",
            color = Color.White,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            Text("Explorar")
        }
        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(id = com.example.senaisp.aplicativomedico.R.drawable.img_bebe),
            contentDescription = "Bebê",
            modifier = Modifier.height(150.dp)
        )
    }
}

@Composable
fun ServicesSection() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFF4A90E2)),
        modifier = Modifier
            .padding(8.dp)
            .shadow(5.dp)
            .width(100.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Serviços",
                color = Color(0xFFFFFFFF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ServiceCard(
            com.example.senaisp.aplicativomedico.R.drawable.ic_ai,
            "IA",
            "Somos uma time que auxilia pais de primeira viagem e pai/mãe solo."
        )
        ServiceCard(
            com.example.senaisp.aplicativomedico.R.drawable.ic_chat,
            "Suporte",
            "Somos uma time que auxilia pais de primeira viagem e pai/mãe solo."
        )
        ServiceCard(
            com.example.senaisp.aplicativomedico.R.drawable.ic_doc,
            "Documentos",
            "Somos uma time que auxilia pais de primeira viagem e pai/mãe solo."
        )
    }
}

@Composable
fun ServiceCard(icon: Int, title: String, desc: String) {
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFFFFF)),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(0.9f)
            .shadow(5.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF103D8C))
            Spacer(Modifier.height(4.dp))
            Text(desc, textAlign = TextAlign.Center, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CalendarSection(navController: NavHostController?) {
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFF4A90E2)),
        modifier = Modifier
            .padding(8.dp)
            .shadow(5.dp)
            .width(260.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Seu calendário de Rotinas",
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(7.dp)
            )
        }
    }
    Text(
        "Acompanhe consultas, vacinas e lembretes importantes para o bem-estar do seu bebê.",
        color = Color.Gray,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 32.dp)
    )
    Spacer(Modifier.height(16.dp))
    MiniCalendar()
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = { navController?.navigate("calendario") },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7986CB)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Ver Calendário", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
    }
    Divider(
        color = Color.Black, // cor da linha
        thickness = 1.dp,        // espessura da linha
        modifier = Modifier
            .fillMaxWidth(0.8f)  // tamanho da linha (80% da largura da tela)
            .padding(vertical = 8.dp)
    )

}
@Composable
fun MiniCalendar() {
    val mesAtual = YearMonth.now()
    val primeiroDia = mesAtual.atDay(1)
    val diasNoMes = mesAtual.lengthOfMonth()
    val primeiroDiaSemana = primeiroDia.dayOfWeek.value % 7
    val hoje = LocalDate.now()

    // Eventos de exemplo (você pode conectar com dados reais depois)
    val eventosExemplo = mapOf(
        1 to Color(0xFFE91E63), // Rosa - Consulta
        5 to Color(0xFF4CAF50), // Verde - Vacina
        10 to Color(0xFFFF9800) // Laranja - Lembrete
    )

    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(Color(0xFFE8EAF6)),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Cabeçalho dos dias da semana
            val diasSemana =
                listOf("Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                diasSemana.forEach { dia ->
                    Text(
                        text = dia.take(3),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5E5E5E)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Grade do calendário
            var diaAtual = 1
            for (semana in 0..5) {
                if (diaAtual > diasNoMes) break

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (diaSemana in 0..6) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if ((semana == 0 && diaSemana < primeiroDiaSemana) || diaAtual > diasNoMes) {
                                // Célula vazia
                            } else {
                                val dia = diaAtual
                                val isHoje = mesAtual.atDay(dia) == hoje
                                val temEvento = eventosExemplo.containsKey(dia)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dia.toString(),
                                        fontSize = 14.sp,
                                        fontWeight = if (isHoje) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isHoje) Color(0xFF7986CB) else Color.Black
                                    )

                                    if (temEvento) {
                                        Spacer(Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(eventosExemplo[dia] ?: Color.Gray)
                                        )
                                    }
                                }
                                diaAtual++
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun RoutineSection(navController: NavHostController?) {
    Spacer(Modifier.height(32.dp))
    Text(
        "Rotina do dia",
        color = Color(0xFF4A90E2),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    Spacer(Modifier.height(8.dp))
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFFFFF)),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .shadow(5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            RoutineItem("Café da manhã", "07:00")
            RoutineItem("Crèche", "08:00")
            RoutineItem("Buscar neném", "11:30")
            RoutineItem("Almoço", "12:00")
        }
    }
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = { navController?.navigate("telarotina") },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
    ) {
        Text("Ver rotina")
    }
}

@Composable
fun RoutineItem(title: String, hour: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = com.example.senaisp.aplicativomedico.R.drawable.ic_doc),
                contentDescription = "Check",
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(title, color = Color.Black, fontSize = 14.sp)
        }
        Text(hour, color = Color.Gray, fontSize = 13.sp)
    }
}

@Composable
fun HistorySection() {
    Divider(
        color = Color.Black, // cor da linha
        thickness = 1.dp,        // espessura da linha
        modifier = Modifier
            .fillMaxWidth(0.8f)  // tamanho da linha (80% da largura da tela)
            .padding(vertical = 8.dp)
    )
    Spacer(Modifier.height(24.dp))
    Text("Histórico", color = Color(0xFF4A90E2), fontWeight = FontWeight.Bold, fontSize = 18.sp)
    Spacer(Modifier.height(8.dp))
    Card(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(Color(0xFFFFFFFF)),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .shadow(5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Exames")
            Divider()
            Text("Dicas")
            Divider()
            Text("Rotinas")
        }
    }
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
    ) {
        Text("Ver historico")
    }
}

@Composable
fun ChatSection() {
    Divider(
        color = Color.Black, // cor da linha
        thickness = 1.dp,        // espessura da linha
        modifier = Modifier
            .fillMaxWidth(0.8f)  // tamanho da linha (80% da largura da tela)
            .padding(vertical = 8.dp)
    )
    Spacer(Modifier.height(24.dp))
    Text("Tire dúvidas rápidas com nossa IA", color = Color.Gray, fontSize = 13.sp)
    Spacer(Modifier.height(8.dp))
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
    ) {
        Text("Baby IA")
    }
    Spacer(Modifier.height(16.dp))
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Digite aqui...") },
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .background(Color.White, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
    )
}

@Composable
fun FooterSection() {
    Spacer(Modifier.height(24.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF4A90E2))
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = com.example.senaisp.aplicativomedico.R.drawable.ic_doc),
            contentDescription = "Instagram",
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("SENAI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen(navegacao = null)
}