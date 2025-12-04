package com.example.senaisp.aplicativomedico.model

import com.google.gson.annotations.SerializedName

// id_chat agora tem valor padrão para facilitar a criação de um novo chat
data class DadosChat(
    val id_chat: Int = 0,
    @SerializedName("user1_id") var userId1: Int = 0,
    @SerializedName("user2_id") var userId2: Int = 0
)

data class ResponseChat(
    val status_code: Int,
    val message: String,
    val chat: DadosChat
)
