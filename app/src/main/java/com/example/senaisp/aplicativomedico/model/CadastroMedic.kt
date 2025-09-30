package com.example.senaisp.aplicativomedico.model

data class CadastroMedic(
    val id: Int = 0,
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val crm: String = "",
    val cpf: String = "",
    val senha: String = "",
    val id_tipo: Int = 2,
)
