package com.example.ecosmile

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LojinhaEcoSmileActivity : AppCompatActivity() {

    private lateinit var txtSaldoLojinha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lojinha_ecosmile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtSaldoLojinha = findViewById(R.id.txtSaldoLojinha)
        val btnVoltar = findViewById<ImageView>(R.id.btnVoltarLojinha)

        val btnResgatarDesconto = findViewById<Button>(R.id.btnResgatarDesconto)
        val btnResgatarEscova = findViewById<Button>(R.id.btnResgatarEscova)

        // Traz o saldo real do usuário logo que a tela abre
        atualizarSaldoNaTela()

        btnVoltar.setOnClickListener { finish() }

        // Conecta os botões com os novos valores super acessíveis
        btnResgatarDesconto.setOnClickListener {
            tentarResgatarProduto("Desconto de 15%", "Aplicável em manutenções", "DESC15", 10)
        }

        btnResgatarEscova.setOnClickListener {
            tentarResgatarProduto("Escova Ecológica Bamboo", "Brinde exclusivo sustentável", "BAMBOO", 15)
        }
    }

    private fun atualizarSaldoNaTela() {
        txtSaldoLojinha.text = "${EcoSmileActivity.saldoGlobal} pts"
    }

    // O Motor de Compra (Conectado com o restante do App)
    private fun tentarResgatarProduto(titulo: String, descricao: String, prefixoCupom: String, custoPontos: Int) {
        // Valida se o usuário tem pontos suficientes na conta global
        if (EcoSmileActivity.saldoGlobal >= custoPontos) {

            // 1. Deduz os pontos da conta
            EcoSmileActivity.saldoGlobal -= custoPontos
            atualizarSaldoNaTela()

            // 2. Coleta a data do momento do resgate
            val dataHoje = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            // 3. Gera um código hash único para o voucher
            val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val hashAleatorio = (1..5).map { caracteres.random() }.joinToString("")
            val codigoCupomGerado = "$prefixoCupom-$hashAleatorio"

            // 4. Salva o cupom na carteira global (Meus Descontos)
            EcoSmileActivity.listaDescontos.add(
                0, // Adiciona sempre no topo da lista
                EcoSmileActivity.DescontoItem(titulo, descricao, codigoCupomGerado, dataHoje)
            )

            // 5. Exibe a comemoração
            mostrarPopUpSucesso(titulo, codigoCupomGerado)

        } else {
            // Se não tiver saldo, avisa exatamente quantos pontos faltam
            val faltam = custoPontos - EcoSmileActivity.saldoGlobal
            Toast.makeText(this, "Saldo insuficiente. Faltam $faltam pts para este item.", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarPopUpSucesso(nomeProduto: String, cupom: String) {
        // Usa o layout Premium de Pop-up que criamos no começo
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val message = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val button = dialogView.findViewById<TextView>(R.id.dialogButton)

        title.text = "Resgate Concluído!"
        message.text = "Você adquiriu: $nomeProduto.\n\nCupom gerado: $cupom\n\nEle já foi salvo na sua aba 'Meus Descontos'!"
        button.text = "VER MINHA CARTEIRA"

        button.setOnClickListener {
            dialog.dismiss()
            finish() // Fecha a lojinha e volta pra tela principal para a pessoa ir ver o desconto
        }

        dialog.show()
    }
}
