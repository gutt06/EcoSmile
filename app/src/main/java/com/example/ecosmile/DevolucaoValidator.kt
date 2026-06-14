package com.example.ecosmile

object DevolucaoValidator {

    // Define os dois resultados possíveis da nossa validação
    sealed class Resultado {
        data class Sucesso(val codigoFormatado: String, val fase: Int, val tipo: String) : Resultado()
        data class Erro(val mensagem: String) : Resultado()
    }

    /**
     * Valida o código seguindo estritamente as regras de negócio:
     * 1. Pertence ao paciente correto
     * 2. Termina em S ou I
     * 3. A fase existe (1 a 17)
     * 4. A fase é estritamente menor que a fase atual do tratamento
     */
    fun validarCodigo(codigoDigitado: String, codigoPaciente: String, faseAtual: Int): Resultado {
        val cod = codigoDigitado.trim().uppercase()

        // Regra 1: Valida o ID do Paciente
        if (!cod.startsWith(codigoPaciente)) {
            return Resultado.Erro("Este código não pertence ao seu tratamento. Ele deve iniciar com '$codigoPaciente'.")
        }

        // Regra 2: Valida se termina em S (Superior) ou I (Inferior)
        if (!cod.endsWith("S") && !cod.endsWith("I")) {
            return Resultado.Erro("Código inválido. O final deve ser obrigatoriamente 'S' (Superior) ou 'I' (Inferior).")
        }

        // Regra 3: Extrai e valida a fase numérica (Ex: de NDCW12S, extrai o "12")
        val faseString = cod.removePrefix(codigoPaciente).dropLast(1)
        val faseDoAlinhador = faseString.toIntOrNull()

        if (faseDoAlinhador == null || faseDoAlinhador !in 1..17) {
            return Resultado.Erro("Fase inválida. Verifique os números digitados (As fases vão de 1 a 17).")
        }

        // Regra 4: Impede devolução da fase atual ou de fases futuras
        if (faseDoAlinhador >= faseAtual) {
            return Resultado.Erro("Você está na Fase $faseAtual. O sistema só permite a devolução de fases anteriores (Fase ${faseAtual - 1} ou inferior).")
        }

        // Passou por todo o funil de segurança!
        val tipo = if (cod.endsWith("S")) "Superior" else "Inferior"
        return Resultado.Sucesso(cod, faseDoAlinhador, tipo)
    }
}
