package com.example.senaisp.aplicativomedico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.senaisp.aplicativomedico.screens.CadastroUser
import com.example.senaisp.aplicativomedico.screens.Cadastroscreen
import com.example.senaisp.aplicativomedico.screens.Chamada
import com.example.senaisp.aplicativomedico.screens.Loginscreen
import com.example.senaisp.aplicativomedico.screens.PerfilMedic
import com.example.senaisp.aplicativomedico.screens.SalaConsulta
import com.example.senaisp.aplicativomedico.ui.theme.AplicativoMedicoTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navegacao = rememberNavController()
            NavHost(
                navController = navegacao,
                startDestination = "chamada"
            ){
                composable(route = "login"){ Loginscreen(navegacao) }
                composable(route = "cadastro"){ CadastroUser(navegacao) }
                composable(route = "perfil"){ PerfilMedic(navegacao) }
                composable(route = "consulta"){ SalaConsulta(navegacao) }
                composable(route = "chamada"){ Chamada(navegacao) }
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