package com.example.senaisp.aplicativomedico.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class Conexao {

    private val BASE_URL = "https://backend-sosbaby.onrender.com/v1/sosbaby/"

    private val conexao= Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun getCadastroMedicoService(): CadastroMedico{
        return conexao.create(CadastroMedico::class.java)
    }

    fun getLoginService(): LoginService{
        return conexao.create(LoginService::class.java)
    }

    fun getCadastroService(): CadastroService{
        return conexao.create(CadastroService::class.java)
    }

    fun getChamadaService(): ChamadaService {
        return conexao.create(ChamadaService::class.java)
    }

    fun getTokenService(): TokenService {
        return conexao.create(TokenService::class.java)
    }


}