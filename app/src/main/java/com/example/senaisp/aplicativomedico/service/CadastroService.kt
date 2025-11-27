package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.CadastroUser
import com.example.senaisp.aplicativomedico.model.ResponseCadastroUser

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CadastroService {

    @Headers("Content-Type: application/json")
    @POST("user/cadastro")
    fun cadastrarUsuario(@Body cliente: CadastroUser): Call<ResponseCadastroUser>

}