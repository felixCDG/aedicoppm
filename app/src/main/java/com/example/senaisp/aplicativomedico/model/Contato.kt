package com.example.senaisp.aplicativomedico.model

data class DadosContato(
    val id_responsavel: Int,
    val nome_responsavel: String,
    val cpf: String,
    val telefone: String,
)

data class ResponseDadosContatos(
    val status: Boolean,
    val status_code: Int,
    val message: String,
    val Nomes: DadosContato
)