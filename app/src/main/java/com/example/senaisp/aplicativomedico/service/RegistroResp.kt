package com.example.senaisp.aplicativomedico.service

import android.telecom.Call
import com.example.senaisp.aplicativomedico.model.RegistroMedico
import com.example.senaisp.aplicativomedico.model.ResponseCadastroMedico
import com.example.senaisp.aplicativomedico.model.ResponsePerfilMedico
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface CadastroMedico {

    @Headers("Content-Type: application/json")
    @POST("doctor/cadastro")
    fun cadastrarResponsavel(@Body cliente: RegistroMedico): retrofit2.Call<ResponseCadastroMedico>

    @Headers("Content-Type: application/json")
    @GET("doctor/{id}")
    fun getMedicoById(@Path("id") id: Int): retrofit2.Call<ResponsePerfilMedico>

}