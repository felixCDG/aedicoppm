package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.BuscarContato
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class BuscarContatoApiResponse(
    val status_code: Int?,
    val message: String?,
    val Quantidade: Int?,
    val Nomes: List<BuscarContato>?
)

interface BuscarContatoInterface {
    @Headers("Content-Type: application/json")
    @POST("filter/nameResp")
    suspend fun buscarContato(
        @Header("Authorization") bearer: String,
        @Body cliente: Map<String, String>
    ): Response<BuscarContatoApiResponse>
}