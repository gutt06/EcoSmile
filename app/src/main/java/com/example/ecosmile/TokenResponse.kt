package com.example.ecosmile

data class TokenResponse(
    val Token_ID: Int,
    val Numero: String,
    val Descricao: String,
    val Data_Geracao: String
)

// Resposta ao gerar um novo token
data class TokenGeradoResponse(
    val sucesso: Boolean,
    val numero: String
)
