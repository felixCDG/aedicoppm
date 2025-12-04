package com.example.senaisp.aplicativomedico.model

import com.google.gson.annotations.SerializedName

data class BuscarContato(
    @SerializedName("id_responsavel") val idResponsavel: Int?,
    @SerializedName("nome_responsavel") val nomeResponsavel: String?,
    @SerializedName("data_nascimento") val dataNascimento: String?,
    @SerializedName("cpf") val cpf: String?,
    @SerializedName("telefone") val telefone: String?,
    @SerializedName("id_user") val idUser: Int?,
    @SerializedName("sexo_responsavel") val sexoResponsavel: String?,
    @SerializedName("nome_user") val nomeUser: String?,
    @SerializedName("usuario_vinculado") val usuarioVinculado: String?
)
