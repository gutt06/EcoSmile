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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistrarDevolucaoActivity : AppCompatActivity() {

    // Cada alinhador adicionado guarda o código já formatado e a fase identificada
    data class Alinhador(val codigo: String, val fase: Int)

    private val codigosAtuais = mutableListOf<Alinhador>()

    // Dados de quem fez login
    private val codigoPacienteLogado = LoginActivity.codigoPacienteLogado
    private val faseAtualDoPaciente = 3

    private val urlApi = "https://api-ecosmile.onrender.com/"

    private lateinit var inputCodigo: EditText
    private lateinit var txtNenhumCodigo: TextView
    private lateinit var containerCodigos: LinearLayout
    private lateinit var btnFinalizar: Button

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
        btnFinalizar = findViewById(R.id.btnFinalizarDevolucao)

        btnVoltar.setOnClickListener { finish() }

        // Ação de Adicionar com a validação simplificada
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
                    if (codigosAtuais.any { it.codigo == resultado.codigoFormatado }) {
                        Toast.makeText(this, "Este código já foi adicionado à lista.", Toast.LENGTH_SHORT).show()
                    } else {
                        codigosAtuais.add(Alinhador(resultado.codigoFormatado, resultado.fase))
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

        // Ação de Finalizar: envia cada código para o banco de dados
        btnFinalizar.setOnClickListener {
            if (codigosAtuais.isEmpty()) {
                Toast.makeText(this, "Adicione pelo menos um código válido antes de finalizar.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarDevolucoesNoBanco()
        }
    }

    private fun registrarDevolucoesNoBanco() {
        // Indica visualmente que o registro está sendo enviado e evita cliques duplicados
        btnFinalizar.isEnabled = false
        btnFinalizar.text = "Enviando..."

        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val pontosPorAlinhador = 50
        val totalCodigos = codigosAtuais.size

        var respostasRecebidas = 0
        var pontosConfirmados = 0

        for (alinhador in codigosAtuais) {
            apiService.devolverAlinhador(
                LoginActivity.idUsuarioLogado,
                alinhador.codigo,
                alinhador.fase,
                pontosPorAlinhador
            ).enqueue(object : Callback<DevolucaoResponse> {
                override fun onResponse(call: Call<DevolucaoResponse>, response: Response<DevolucaoResponse>) {
                    respostasRecebidas++

                    val body = response.body()
                    if (response.isSuccessful && body?.sucesso == true) {
                        pontosConfirmados += pontosPorAlinhador
                    } else {
                        val mensagem = body?.mensagem ?: "Erro ao registrar o código ${alinhador.codigo}."
                        Toast.makeText(this@RegistrarDevolucaoActivity, mensagem, Toast.LENGTH_LONG).show()
                    }

                    if (respostasRecebidas == totalCodigos) {
                        finalizarRegistro(pontosConfirmados)
                    }
                }

                override fun onFailure(call: Call<DevolucaoResponse>, t: Throwable) {
                    respostasRecebidas++
                    Toast.makeText(this@RegistrarDevolucaoActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()

                    if (respostasRecebidas == totalCodigos) {
                        finalizarRegistro(pontosConfirmados)
                    }
                }
            })
        }
    }

    private fun finalizarRegistro(pontosConfirmados: Int) {
        // Restaura o botão para o estado normal
        btnFinalizar.isEnabled = true
        btnFinalizar.text = "Finalizar registro de devolução"

        if (pontosConfirmados > 0) {
            mostrarPopUpSucesso(pontosConfirmados)
        }
    }

    private fun atualizarInterfaceItens() {
        if (codigosAtuais.isNotEmpty()) {
            txtNenhumCodigo.visibility = View.GONE
        } else {
            txtNenhumCodigo.visibility = View.VISIBLE
        }

        containerCodigos.removeAllViews()

        for (alinhador in codigosAtuais) {
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
                text = alinhador.codigo
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
