package com.example.ecosmile

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    // Metodo para Login
    @FormUrlEncoded
    @POST("login.php")
    fun login(
        @Field("email") email: String,
        @Field("senha") senha: String
    ): Call<List<LoginResponse>>

    // Metodo para Cadastro
    @FormUrlEncoded
    @POST("cadastro.php")
    fun cadastrar(
        @Field("nome") nome: String,
        @Field("email") email: String,
        @Field("senha") senha: String,
        @Field("cpf") cpf: String? = null
    ): Call<UnificadoResponse>

    // Metodo para Troca de Senha
    @FormUrlEncoded
    @POST("trocar_senha.php")
    fun trocarSenha(
        @Field("email") email: String,
        @Field("nova_senha") novaSenha: String
    ): Call<TrocarSenhaResponse>

    // Metodo para buscar o saldo de pontos do usuario
    @GET("buscar_saldo.php")
    fun buscarSaldo(
        @Query("usuario_id") usuarioId: Int
    ): Call<SaldoResponse>

    // Metodo para buscar o historico de devolucoes do usuario
    @GET("buscar_historico.php")
    fun buscarHistorico(
        @Query("usuario_id") usuarioId: Int
    ): Call<List<HistoricoResponse>>

    // Metodo para buscar os cupons resgatados pelo usuario
    @GET("buscar_cupons.php")
    fun buscarCupons(
        @Query("usuario_id") usuarioId: Int
    ): Call<List<CupomResponse>>

    // Metodo para registrar a devolucao de um alinhador
    @FormUrlEncoded
    @POST("devolver_alinhador.php")
    fun devolverAlinhador(
        @Field("usuario_id") usuarioId: Int,
        @Field("codigo_alinhador") codigoAlinhador: String,
        @Field("fase") fase: Int,
        @Field("pontos") pontos: Int,
        @Field("ponto_coleta_id") pontoColetaId: Int? = null
    ): Call<DevolucaoResponse>

    // Metodo para resgatar um item da lojinha (desconto ou produto)
    @FormUrlEncoded
    @POST("resgatar_cupom.php")
    fun resgatarCupom(
        @Field("usuario_id") usuarioId: Int,
        @Field("produto_id") produtoId: Int
    ): Call<ResgateResponse>

    // Metodo para buscar a configuracao atual de pontos por alinhador
    @GET("buscar_config.php")
    fun buscarConfig(): Call<ConfigResponse>

    // Metodo para salvar a configuracao de pontos por alinhador
    @FormUrlEncoded
    @POST("salvar_config.php")
    fun salvarConfig(
        @Field("pontos_por_alinhador") pontosPorAlinhador: Int
    ): Call<ConfigResponse>

    // Metodo para buscar os itens (descontos/produtos) disponiveis na Lojinha
    @GET("buscar_produtos_lojinha.php")
    fun buscarProdutosLojinha(): Call<List<ProdutoLojinhaResponse>>

    // Metodo para buscar os pontos de coleta disponiveis
    @GET("buscar_pontos_coleta.php")
    fun buscarPontosColeta(): Call<List<PontoColetaResponse>>

    // Rotas de admin

    // login
    @GET("login_admin.php")
    fun loginAdmin(
        @Query("email") email: String,
        @Query("senha") senha: String
    ): Call<List<AdminResponse>>

    // criar item (desconto ou produto) para a lojinha
    @FormUrlEncoded
    @POST("criar_produto_lojinha.php")
    fun criarProdutoLojinha(
        @Field("tipo") tipo: String,
        @Field("titulo") titulo: String,
        @Field("descricao") descricao: String? = null,
        @Field("valor_desconto") valorDesconto: String? = null,
        @Field("custo_pontos") custoPontos: Int
    ): Call<MensagemResponse>

    // criar ponto de coleta
    @FormUrlEncoded
    @POST("criar_ponto_coleta.php")
    fun criarPontoColeta(
        @Field("nome") nome: String,
        @Field("endereco") endereco: String,
        @Field("horario") horario: String
    ): Call<MensagemResponse>
}
