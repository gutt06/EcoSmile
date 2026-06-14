package com.example.ecosmile

object DevolucaoValidator {

    // Define os dois resultados possíveis da nossa validação
    sealed class Resultado {
        data class Sucesso(val codigoFormatado: String, val fase: Int) : Resultado()
        data class Erro(val mensagem: String) : Resultado()
    }

    /**
     * Validação simplificada: extrai o número da fase do código digitado,
     * impede a devolução de fases futuras (maiores que a fase atual)
     * e completa o sufixo com "S" (Superior) caso não termine em S ou I.
     */
    fun validarCodigo(codigoDigitado: String, codigoPaciente: String, faseAtual: Int): Resultado {
        val cod = codigoDigitado.trim().uppercase()

        val regexNumero = Regex("(\\d+)")
        val match = regexNumero.find(cod)

        if (match == null) {
            return Resultado.Erro("Código inválido. O código precisa ter pelo menos um número (Ex: 1, 2, NDCW1).")
        }

        val faseDevolvida = match.groupValues[1].toIntOrNull() ?: 0

        if (faseDevolvida > faseAtual) {
            return Resultado.Erro("Fase inválida! Você está na Fase $faseAtual. Não pode devolver um alinhador do futuro (Fase $faseDevolvida).")
        }

        var codigoFinal = cod
        if (!cod.endsWith("S") && !cod.endsWith("I")) {
            codigoFinal += "S"
        }

        return Resultado.Sucesso(codigoFormatado = codigoFinal, fase = faseDevolvida)
    }
}
