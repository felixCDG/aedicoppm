package com.example.senaisp.aplicativomedico.service

import com.example.senaisp.aplicativomedico.model.Login
import com.example.senaisp.aplicativomedico.model.ResponseLoginUser

import retrofit2.Call
import retrofit2.http.Body

import retrofit2.http.Headers
import retrofit2.http.POST


interface LoginService {

    @Headers("Content-Type: application/json")
    @POST("user/login")
    fun loginUsuario(@Body cliente: Login): Call<ResponseLoginUser>


}