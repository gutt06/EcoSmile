package com.example.ecosmile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EcoSmileActivity : AppCompatActivity() {

    private val urlApi = "https://api-ecosmile.onrender.com/"

    private lateinit var txtSaldoPontos: TextView
    private lateinit var containerHistorico: LinearLayout
    private lateinit var cardHistoricoVazio: MaterialCardView
    private lateinit var cardHistoricoCarregando: MaterialCardView
    private lateinit var containerPontosColeta: LinearLayout
    private lateinit var cardPontosColetaVazio: MaterialCardView
    private lateinit var cardPontosColetaCarregando: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_ecosmile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtSaldoPontos = findViewById(R.id.txtSaldoPontos)
        containerHistorico = findViewById(R.id.containerHistorico)
        cardHistoricoVazio = findViewById(R.id.cardHistoricoVazio)
        cardHistoricoCarregando = findViewById(R.id.cardHistoricoCarregando)
        containerPontosColeta = findViewById(R.id.containerPontosColeta)
        cardPontosColetaVazio = findViewById(R.id.cardPontosColetaVazio)
        cardPontosColetaCarregando = findViewById(R.id.cardPontosColetaCarregando)

        val btnVoltar = findViewById<ImageView>(R.id.btnVoltarEco)
        val btnRegistrarDevolucao = findViewById<Button>(R.id.btnRegistrarDevolucao)
        val btnAcessarLojinha = findViewById<Button>(R.id.btnAcessarLojinha)
        val btnEntendaImpacto = findViewById<Button>(R.id.btnEntendaImpacto)
        val btnVerPassoAPasso = findViewById<Button>(R.id.btnVerPassoAPasso)

        btnVoltar.setOnClickListener { finish() }

        btnRegistrarDevolucao.setOnClickListener {
            startActivity(Intent(this, RegistrarDevolucaoActivity::class.java))
        }

        btnAcessarLojinha.setOnClickListener {
            startActivity(Intent(this, LojinhaEcoSmileActivity::class.java))
        }

        btnEntendaImpacto.setOnClickListener {
            startActivity(Intent(this, MeuImpactoActivity::class.java))
        }

        btnVerPassoAPasso.setOnClickListener {
            exibirPopUpPassoAPasso()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosDoBanco()
    }

    private fun carregarDadosDoBanco() {
        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val usuarioId = LoginActivity.idUsuarioLogado

        // Indica visualmente que os dados estão sendo (re)carregados
        txtSaldoPontos.text = "..."

        // 1. Busca o saldo de pontos atual
        apiService.buscarSaldo(usuarioId).enqueue(object : Callback<SaldoResponse> {
            override fun onResponse(call: Call<SaldoResponse>, response: Response<SaldoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    txtSaldoPontos.text = response.body()!!.saldo.toString()
                } else {
                    txtSaldoPontos.text = "0"
                }
            }

            override fun onFailure(call: Call<SaldoResponse>, t: Throwable) {
                txtSaldoPontos.text = "0"
            }
        })

        // 2. Busca o histórico de devoluções
        apiService.buscarHistorico(usuarioId).enqueue(object : Callback<List<HistoricoResponse>> {
            override fun onResponse(call: Call<List<HistoricoResponse>>, response: Response<List<HistoricoResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    renderizarHistorico(response.body()!!)
                } else {
                    renderizarHistorico(emptyList())
                }
            }

            override fun onFailure(call: Call<List<HistoricoResponse>>, t: Throwable) {
                renderizarHistorico(emptyList())
            }
        })

        // 3. Busca os pontos de coleta disponíveis
        apiService.buscarPontosColeta().enqueue(object : Callback<List<PontoColetaResponse>> {
            override fun onResponse(call: Call<List<PontoColetaResponse>>, response: Response<List<PontoColetaResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    renderizarPontosColeta(response.body()!!)
                } else {
                    renderizarPontosColeta(emptyList())
                }
            }

            override fun onFailure(call: Call<List<PontoColetaResponse>>, t: Throwable) {
                renderizarPontosColeta(emptyList())
            }
        })
    }

    private fun renderizarPontosColeta(lista: List<PontoColetaResponse>) {
        containerPontosColeta.removeAllViews()

        // Os dados chegaram (ou falharam): esconde o indicador de carregamento
        cardPontosColetaCarregando.visibility = View.GONE

        // Lógica de Visibilidade: Controla se mostra a lista ou o card de estado vazio
        if (lista.isEmpty()) {
            cardPontosColetaVazio.visibility = View.VISIBLE
            containerPontosColeta.visibility = View.GONE
            return
        } else {
            cardPontosColetaVazio.visibility = View.GONE
            containerPontosColeta.visibility = View.VISIBLE
        }

        fun Int.dpToPx() = (this * resources.displayMetrics.density).toInt()

        val fonteSfPro = ResourcesCompat.getFont(this, R.font.sf_pro)

        for (item in lista) {
            val cardView = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 12.dpToPx()) }
                radius = 20.dpToPx().toFloat()
                strokeWidth = (1.5 * resources.displayMetrics.density).toInt()
                setStrokeColor(Color.parseColor("#FFD4C4"))
                cardElevation = 0f
                setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            val layoutItem = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(20.dpToPx(), 20.dpToPx(), 20.dpToPx(), 20.dpToPx())
            }

            val icone = ImageView(this).apply {
                setImageResource(android.R.drawable.ic_menu_mylocation)
                setColorFilter(Color.parseColor("#F97553"))
                layoutParams = LinearLayout.LayoutParams(24.dpToPx(), 24.dpToPx()).apply {
                    marginEnd = 16.dpToPx()
                }
            }

            val colTexto = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
            }

            val txtNome = TextView(this).apply {
                text = item.nome
                textSize = 15f
                setTypeface(fonteSfPro, Typeface.BOLD)
                setTextColor(Color.parseColor("#111111"))
            }

            val txtDetalhe = TextView(this).apply {
                text = "${item.endereco} • ${item.horario}"
                textSize = 12f
                typeface = fonteSfPro
                setTextColor(Color.parseColor("#999999"))
            }

            colTexto.addView(txtNome)
            colTexto.addView(txtDetalhe)

            layoutItem.addView(icone)
            layoutItem.addView(colTexto)
            cardView.addView(layoutItem)
            containerPontosColeta.addView(cardView)
        }
    }

    private fun exibirPopUpPassoAPasso() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val message = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val button = dialogView.findViewById<TextView>(R.id.dialogButton)

        title.text = "Passos para Descarte"
        message.text = "1. Lave bem seus alinhadores antigos com sabão neutro e água corrente.\n\n" +
                "2. Seque totalmente para evitar microorganismos.\n\n" +
                "3. Insira na embalagem e entregue na recepção com seus códigos gerados!"
        button.text = "Entendi"

        button.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun renderizarHistorico(lista: List<HistoricoResponse>) {
        containerHistorico.removeAllViews()

        // Os dados chegaram (ou falharam): esconde o indicador de carregamento
        cardHistoricoCarregando.visibility = View.GONE

        // Lógica de Visibilidade: Controla se mostra a lista ou o card de estado vazio
        if (lista.isEmpty()) {
            cardHistoricoVazio.visibility = View.VISIBLE
            containerHistorico.visibility = View.GONE
            return
        } else {
            cardHistoricoVazio.visibility = View.GONE
            containerHistorico.visibility = View.VISIBLE
        }

        for (item in lista) {
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

            val colTexto = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f)
            }

            val txtCod = TextView(this).apply {
                text = item.codigoAlinhador
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#111111"))
            }

            val txtSub = TextView(this).apply {
                text = "${item.dataDevolucao} - Fase ${item.fase}"
                textSize = 12f
                setTextColor(Color.parseColor("#777777"))
                setPadding(0, 4, 0, 0)
            }

            colTexto.addView(txtCod)
            colTexto.addView(txtSub)

            val txtPts = TextView(this).apply {
                text = "+${item.pontosGerados} pts"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#F97553"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f).apply {
                    gravity = Gravity.END or Gravity.CENTER_VERTICAL
                }
                gravity = Gravity.END
            }

            layoutItem.addView(colTexto)
            layoutItem.addView(txtPts)
            cardView.addView(layoutItem)
            containerHistorico.addView(cardView)
        }
    }
}
