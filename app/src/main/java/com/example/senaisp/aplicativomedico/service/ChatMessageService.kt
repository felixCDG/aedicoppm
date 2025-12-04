package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.ResponseMensagens
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// Serviço para obter mensagens de um chat por id
interface ChatMessageService {
    @GET("chat/message/{id}")
    fun getMessagesByChatId(@Path("id") id: Int): Call<ResponseMensagens>
}
