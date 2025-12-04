package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.DadosChat
import com.example.senaisp.aplicativomedico.model.DadosMensagem
import com.example.senaisp.aplicativomedico.model.ResponseChat
import com.example.senaisp.aplicativomedico.model.ResponseMensagem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CadastroMensagen {

    @Headers("Content-Type: application/json")
    @POST("message/send")
    fun cadastrarMensagem(@Body cliente: DadosMensagem): Call<ResponseMensagem>

}