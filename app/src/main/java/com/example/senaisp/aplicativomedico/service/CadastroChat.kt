package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.CadastroUser
import com.example.senaisp.aplicativomedico.model.DadosChat
import com.example.senaisp.aplicativomedico.model.ResponseCadastroUser
import com.example.senaisp.aplicativomedico.model.ResponseChat
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CadastroChat {

    @Headers("Content-Type: application/json")
    @POST("chat/cadastro")
    fun cadastrarChat(@Body cliente: DadosChat): Call<ResponseChat>

}