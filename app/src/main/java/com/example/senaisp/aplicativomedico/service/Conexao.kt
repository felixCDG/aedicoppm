package com.example.senaisp.aplicativomedico.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Conexao {

    // Atenção: garanta que sua URL termine com '/' e que não tenha espaços extras.
    // Ex: "https://backend-sosbaby.onrender.com/v1/sosbaby/"
    private const val BASE_URL = "https://backend-sosbaby.onrender.com/v1/sosbaby/"

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val conexao: Retrofit by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getCadastroMedicoService(): CadastroMedico {
        return conexao.create(CadastroMedico::class.java)
    }

    fun getLoginService(): LoginService {
        return conexao.create(LoginService::class.java)
    }

    fun getCadastroService(): CadastroService {
        return conexao.create(CadastroService::class.java)
    }

    fun getChamadaService(): ChamadaService {
        return conexao.create(ChamadaService::class.java)
    }

    fun getTokenService(): TokenService {
        return conexao.create(TokenService::class.java)
    }

    fun getBuscarContatoService(): BuscarContatoInterface {
        return conexao.create(BuscarContatoInterface::class.java)
    }

    fun getCadastrarChatService(): CadastroChat {
        return conexao.create(CadastroChat::class.java)
    }

    fun getChatMessageService(): ChatMessageService {
        return conexao.create(ChatMessageService::class.java)
    }

    fun getMessageService(): CadastroMensagen {
        return conexao.create(CadastroMensagen::class.java)
    }

}