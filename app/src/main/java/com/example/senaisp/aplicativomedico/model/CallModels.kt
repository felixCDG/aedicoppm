package com.example.senaisp.aplicativomedico.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

// Resposta para criar chamada
data class CreateChamadaResponse(
    val status: Boolean?,
    @SerializedName("status_code") val statusCode: Int?,
    val message: String?,
    val data: ChamadaData?
)

data class ChamadaData(
    @SerializedName("id_chamada") val idChamada: Int?,
    @SerializedName("nome_chamada") val nomeChamada: String?,
    @SerializedName("criado_em") val criadoEm: String?
)

// Envelope do token — token pode vir de formas diferentes; usar JsonElement para ser robusto
data class TokenEnvelope(
    @SerializedName("status_code") val statusCode: Int? = null,
    val message: String? = null,
    val userData: UserData? = null,
    // token pode ser string ou objeto — receber como JsonElement e tratar depois
    val token: JsonElement? = null,
    // campos diretos no envelope (caso apareçam)
    val identity: String? = null,
    @SerializedName(value = "room", alternate = ["Room"]) val room: String? = null
)

data class UserData(
    val id: Int?,
    val nome: String?,
    val tipo: Int? = null
)
