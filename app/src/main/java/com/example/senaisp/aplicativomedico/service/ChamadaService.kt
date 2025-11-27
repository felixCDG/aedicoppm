package com.example.senaisp.aplicativomedico.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.ResponseBody

// Request model usado pela API para criar uma chamada
data class ChamadaRequest(
    val nome_chamada: String
)

interface ChamadaService {

    @Headers("Content-Type: application/json")
    @POST("chamada/cadastro")
    fun criarChamada(@Header("Authorization") auth: String?, @Body body: ChamadaRequest): Call<ResponseBody>

}
