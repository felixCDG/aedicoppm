package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.TokenEnvelope
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface TokenService {
    @Headers("Content-Type: application/json")
    @POST("call/token")
    fun requestToken(@Header("Authorization") auth: String?, @Body body: Map<String, String>): Call<TokenEnvelope>
}
