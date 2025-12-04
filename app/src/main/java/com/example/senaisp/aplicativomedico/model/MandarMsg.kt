package com.example.senaisp.aplicativomedico.model

import com.google.gson.annotations.SerializedName

data class DadosMensagem(
    val conteudo: String,
    @SerializedName("id_chat") var idChat: Int = 0,
    @SerializedName("id_user") var idUser: Int = 0
)

data class ResponseMensagem(
    val status: Boolean,
    val status_code: Int,
    val message: String,
    val chat: DadosMensagem
)