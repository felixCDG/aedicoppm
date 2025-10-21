package com.example.senaisp.aplicativomedico.model

import com.google.gson.annotations.SerializedName

data class RegistroMedico(

    val id_medico: Int = 0,
    val nome: String = "",
    val email: String = "",
    val telefone: String = "",
    val crm: String = "",
    val cpf: String = "",
    val foto: String = "",
    @SerializedName("id_sexo") var idSexo: Int = 0,
    val id_user: Int =1,

)
