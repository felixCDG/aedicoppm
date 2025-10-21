package com.example.senaisp.aplicativomedico.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

class Conexao {

    private val BASE_URL = "http://10.0.2.2:3030/v1/sosbaby/"

    private val conexao= Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun getCadastroMedicoService(): CadastroMedico{
        return conexao.create(CadastroMedico::class.java)
    }




}