package com.example.senaisp.aplicativomedico.model


data class CadastroUserData(
    val id_user: Int,
    val email: String,
    val senha: String,
    val id_tipo: Int
)

data class ResponseCadastroUser(
    val status: Boolean,
    val status_code: Int,
    val message: String,
    val data: CadastroUserData
)