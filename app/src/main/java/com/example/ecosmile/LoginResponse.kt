package com.example.ecosmile

data class LoginResponse(
    val usuarioId: String?,
    val usuarioNome: String,
    val usuarioEmail: String?,
    val usuarioCpf: String?,
    val codigoPaciente: String?,
    val saldoPontos: Int?
)
