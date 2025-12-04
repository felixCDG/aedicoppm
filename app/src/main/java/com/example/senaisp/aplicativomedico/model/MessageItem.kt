package com.example.senaisp.aplicativomedico.model

import com.google.gson.annotations.SerializedName

// Modelo atualizado para a resposta real da API
data class MessageItem(
    @SerializedName("id_mensagem") val idMensagem: Int,
    @SerializedName("conteudo") val conteudo: String?,
    @SerializedName("id_chat") val idChat: Int,
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("nome_user") val nomeUser: String?,
    @SerializedName("created_at") val createdAt: String?
)

// Wrapper retornado pelo endpoint GET /chat/message/{id}
data class ResponseMensagens(
    @SerializedName("status_code") val status_code: Int,
    val message: String?,
    val itens: Int,
    val mensagens: List<MessageItem>
)
