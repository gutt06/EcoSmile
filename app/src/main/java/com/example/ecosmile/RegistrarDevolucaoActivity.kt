package com.example.ecosmile

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarDevolucaoActivity : AppCompatActivity() {

    private val codigosAtuais = mutableListOf<String>()

    // Variáveis que futuramente virão do Banco de Dados/Login
    private val codigoPacienteLogado = "NDCW"
    private val faseAtualDoPaciente = 3

    private lateinit var inputCodigo: EditText
    private lateinit var txtNenhumCodigo: TextView
    private lateinit var containerCodigos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_devolucao)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputCodigo = findViewById(R.id.inputCodigoAlinhador)
        txtNenhumCodigo = findViewById(R.id.txtNenhumCodigo)
        containerCodigos = findViewById(R.id.containerCodigosRegistrados)

        val btnVoltar = findViewById<ImageView>(R.id.btnVoltarDevolucao)
        val btnAdicionar = findViewById<Button>(R.id.btnAdicionarCodigo)
        val btnFinalizar = findViewById<Button>(R.id.btnFinalizarDevolucao)

        btnVoltar.setOnClickListener { finish() }

        // Ação de Adicionar com a NOVA LÓGICA DE VALIDAÇÃO
        btnAdicionar.setOnClickListener {
            val codigoDigitado = inputCodigo.text.toString()

            if (codigoDigitado.isBlank()) {
                Toast.makeText(this, "Por favor, digite um código.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chama o nosso motor de segurança
            val resultado = DevolucaoValidator.validarCodigo(
                codigoDigitado = codigoDigitado,
                codigoPaciente = codigoPacienteLogado,
                faseAtual = faseAtualDoPaciente
            )

            when (resultado) {
                is DevolucaoValidator.Resultado.Sucesso -> {
                    // Verifica se o usuário já não adicionou esse mesmo código nesta sessão
                    if (codigosAtuais.contains(resultado.codigoFormatado)) {
                        Toast.makeText(this, "Este código já foi adicionado à lista.", Toast.LENGTH_SHORT).show()
                    } else {
                        codigosAtuais.add(resultado.codigoFormatado)
                        inputCodigo.text.clear()
                        atualizarInterfaceItens()
                    }
                }
                is DevolucaoValidator.Resultado.Erro -> {
                    // Exibe a mensagem exata do erro que bloqueou a ação
                    Toast.makeText(this, resultado.mensagem, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Ação de Finalizar (Mantida igual, salva no histórico global)
        btnFinalizar.setOnClickListener {
            if (codigosAtuais.isEmpty()) {
                Toast.makeText(this, "Adicione pelo menos um código válido antes de finalizar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pontosGanhos = codigosAtuais.size * 50
            val dataHoje = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            EcoSmileActivity.saldoGlobal += pontosGanhos

            for (codigo in codigosAtuais) {
                EcoSmileActivity.listaHistorico.add(
                    0,
                    EcoSmileActivity.HistoricoItem(codigo, dataHoje, "Fase Anterior", "+50 pts")
                )
            }

            mostrarPopUpSucesso(pontosGanhos)
        }
    }

    private fun atualizarInterfaceItens() {
        if (codigosAtuais.isNotEmpty()) {
            txtNenhumCodigo.visibility = View.GONE
        } else {
            txtNenhumCodigo.visibility = View.VISIBLE
        }

        containerCodigos.removeAllViews()

        for (codigo in codigosAtuais) {
            val cardView = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16) }
                radius = 40f
                strokeWidth = 2
                setStrokeColor(Color.parseColor("#F5F5F7"))
                cardElevation = 0f
            }

            val layoutItem = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(48, 40, 48, 40)
                weightSum = 2f
            }

            val txtCod = TextView(this).apply {
                text = codigo
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#111111"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f)
            }

            val txtPts = TextView(this).apply {
                text = "+50 pts"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#F97553"))
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f)
            }

            layoutItem.addView(txtCod)
            layoutItem.addView(txtPts)
            cardView.addView(layoutItem)
            containerCodigos.addView(cardView)
        }
    }

    private fun mostrarPopUpSucesso(pontosTotais: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val message = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val button = dialogView.findViewById<TextView>(R.id.dialogButton)

        title.text = "Devolução Registrada!"
        message.text = "Você acaba de ganhar +$pontosTotais pontos virtuais no EcoSmile pela reciclagem correta de suas fases antigas. Eles já estão na sua carteira!"
        button.text = "CONTINUAR"

        button.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
