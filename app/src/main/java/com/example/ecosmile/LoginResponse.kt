package com.example.ecosmile

data class LoginResponse(
    val usuarioId: Int,
    val usuarioNome: String,
    val usuarioEmail: String,
    val usuarioSenha: String
)
