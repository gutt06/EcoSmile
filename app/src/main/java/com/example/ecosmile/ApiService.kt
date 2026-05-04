package com.example.ecosmile

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Metodo para Login
    @GET("/apis/login.php")
    fun login(
        @Query("email") email: String,
        @Query("senha") senha: String
    ): Call<List<LoginResponse>>

    // Metodo para Cadastro
    @FormUrlEncoded
    @POST("/apis/cadastro.php")
    fun cadastrar(
        @Field("nome") nome: String,
        @Field("email") email: String,
        @Field("senha") senha: String,
    ): Call<Void>

    // Metodo para Troca de Senha
    @FormUrlEncoded
    @POST("/apis/trocar_senha.php")
    fun trocarSenha(
        @Field("email") email: String,
        @Field("nova_senha") novaSenha: String
    ): Call<Void>
}