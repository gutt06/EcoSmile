package com.example.ecosmile

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Metodo para Login
    @GET("login.php")
    fun login(
        @Query("email") email: String,
        @Query("senha") senha: String
    ): Call<List<LoginResponse>>

    // Metodo para Cadastro
    @FormUrlEncoded
    @POST("cadastro.php")
    fun cadastrar(
        @Field("nome") nome: String,
        @Field("email") email: String,
        @Field("senha") senha: String,
    ): Call<Void>

    // Metodo para Troca de Senha
    @FormUrlEncoded
    @POST("trocar_senha.php")
    fun trocarSenha(
        @Field("email") email: String,
        @Field("nova_senha") novaSenha: String
    ): Call<Void>

    // Rotas de admin

    // login
    @GET("login_admin.php")
    fun loginAdmin(
        @Query("email") email: String,
        @Query("senha") senha: String
    ): Call<List<AdminResponse>>

    // tipo de consulta
    @FormUrlEncoded
    @POST("criar_tipo_consulta.php")
    fun criarTipoConsulta(
        @Field("descricao") descricao: String,
        @Field("unidade") unidade: String,
        @Field("valor") valor: String
    ): Call<Void>

    // tipo de desconto
    @FormUrlEncoded
    @POST("criar_tipo_desconto.php")
    fun criarTipoDesconto(
        @Field("descricao") descricao: String,
        @Field("expira") expira: String,
        @Field("valor") valor: String
    ): Call<Void>

    // ponto de coleta
    @FormUrlEncoded
    @POST("criar_ponto_coleta.php")
    fun criarPontoColeta(
        @Field("nome") nome: String,
        @Field("endereco") endereco: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("horario") horario: String
    ): Call<Void>

    // token
    @GET("token.php")
    fun listarTokens(): Call<List<TokenResponse>>

    @FormUrlEncoded
    @POST("token.php")
    fun gerarToken(
        @Field("tipoDesconto_ID") tipoDescontoId: String
    ): Call<TokenGeradoResponse>
}