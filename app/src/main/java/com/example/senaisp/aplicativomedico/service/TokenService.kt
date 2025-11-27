package com.example.senaisp.aplicativomedico.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

// O backend às vezes retorna { token: { token, identity, room } } ou { token: string, identity: Room }
data class TokenEnvelope(
    val token: Any? = null,
    val identity: String? = null,
    @SerializedName(value = "Room", alternate = ["room"]) val room: String? = null
)

interface TokenService {
    @Headers("Content-Type: application/json")
    @POST("call/token")
    fun requestToken(@Header("Authorization") auth: String?, @Body body: Map<String, String>): Call<TokenEnvelope>
}
