package com.example.ecosmile

// Modelos de resposta dos novos endpoints do EcoSmile (cadastro, troca de senha,
// cupons, devolução de alinhadores, saldo e histórico)

data class UnificadoResponse(
    val sucesso: Boolean,
    val mensagem: String?,
    val usuarioNome: String?,
    val usuarioId: Int?,
    val codigoPaciente: String?
)

data class TrocarSenhaResponse(
    val status: String,
    val mensagem: String
)

data class CupomResponse(
    val titulo: String,
    val descricao: String,
    val codigoCupom: String,
    val dataResgate: String
)

data class DevolucaoResponse(
    val sucesso: Boolean,
    val mensagem: String,
    val novoSaldo: Int?
)

data class ResgateResponse(
    val sucesso: Boolean,
    val mensagem: String,
    val codigoCupom: String?
)

data class SaldoResponse(
    val sucesso: Boolean,
    val saldo: Int
)

data class HistoricoResponse(
    val codigoAlinhador: String,
    val fase: Int,
    val dataDevolucao: String,
    val pontosGerados: Int
)
