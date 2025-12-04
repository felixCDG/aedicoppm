package com.example.senaisp.aplicativomedico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.senaisp.aplicativomedico.screens.CadastroUser
import com.example.senaisp.aplicativomedico.screens.ContatosScreen
import com.example.senaisp.aplicativomedico.screens.VideoChamadaScreen
import com.example.senaisp.aplicativomedico.screens.Loginscreen
import com.example.senaisp.aplicativomedico.screens.PerfilMedic
import com.example.senaisp.aplicativomedico.screens.SalaConsulta
import com.example.senaisp.aplicativomedico.screens.CreateCallScreen
import com.example.senaisp.aplicativomedico.screens.HomeScreen
import com.example.senaisp.aplicativomedico.ui.theme.AplicativoMedicoTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navegacao = rememberNavController()
            NavHost(
                navController = navegacao,
                startDestination = "cadastro"
            ){
                composable(route = "login"){ Loginscreen(navegacao) }
                composable(route = "cadastro"){ CadastroUser(navegacao) }
                composable(route = "perfil"){ PerfilMedic(navegacao) }
                composable(route = "home"){ HomeScreen(navegacao) }
                composable(route = "contatos"){ ContatosScreen(navegacao) }
                composable(route = "consulta"){ SalaConsulta(navegacao) }
                composable(route = "chamada"){ VideoChamadaScreen(navegacao) }
                // Screen to create a call (optional route if you want to navigate there)
                composable(route = "create_call"){ CreateCallScreen(navegacao) }
                // Parametrized video route receiving a room name and forwarding to VideoChamadaScreen
                composable(
                    route = "video/{room}",
                    arguments = listOf(navArgument("room"){ type = NavType.StringType })
                ) { backStackEntry ->
                    val room = backStackEntry.arguments?.getString("room")
                    VideoChamadaScreen(navegacao, room)
                }
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplicativoMedicoTheme {
        Greeting("Android")
    }
}