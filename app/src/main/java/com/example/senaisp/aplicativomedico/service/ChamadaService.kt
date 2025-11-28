package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.CreateChamadaResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class ChamadaRequest(
    val nome_chamada: String
)

interface ChamadaService {
    @Headers("Content-Type: application/json")
    @POST("chamada/cadastro")
    fun criarChamada(@Header("Authorization") auth: String?, @Body body: ChamadaRequest): Call<CreateChamadaResponse>
}
