package com.example.ecosmile

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

class MeusDescontosActivity : AppCompatActivity() {

    private val urlApi = "https://api-ecosmile.onrender.com/"

    private lateinit var cardVazioDescontos: MaterialCardView
    private lateinit var cardDescontosCarregando: MaterialCardView
    private lateinit var containerDescontos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_meus_descontos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cardVazioDescontos = findViewById(R.id.cardVazioDescontos)
        cardDescontosCarregando = findViewById(R.id.cardDescontosCarregando)
        containerDescontos = findViewById(R.id.containerDescontos)
        val btnVoltar = findViewById<ImageView>(R.id.btnVoltarDescontos)

        btnVoltar.setOnClickListener { finish() }

        buscarCuponsDoBanco()
    }

    private fun buscarCuponsDoBanco() {
        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.buscarCupons(LoginActivity.idUsuarioLogado).enqueue(object : Callback<List<CupomResponse>> {
            override fun onResponse(call: Call<List<CupomResponse>>, response: Response<List<CupomResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val listaDeCupons = response.body()!!
                    desenharCuponsNaTela(listaDeCupons)
                } else {
                    desenharCuponsNaTela(emptyList())
                }
            }

            override fun onFailure(call: Call<List<CupomResponse>>, t: Throwable) {
                Toast.makeText(this@MeusDescontosActivity, "Erro ao buscar cupons: ${t.message}", Toast.LENGTH_SHORT).show()
                desenharCuponsNaTela(emptyList())
            }
        })
    }

    private fun desenharCuponsNaTela(lista: List<CupomResponse>) {
        // Os dados chegaram (ou falharam): esconde o indicador de carregamento
        cardDescontosCarregando.visibility = View.GONE

        // Controle do Estado Vazio
        if (lista.isEmpty()) {
            cardVazioDescontos.visibility = View.VISIBLE
            containerDescontos.visibility = View.GONE
            return
        } else {
            cardVazioDescontos.visibility = View.GONE
            containerDescontos.visibility = View.VISIBLE
        }

        containerDescontos.removeAllViews()

        // Função para converter medidas e ficar perfeito em qualquer tela
        fun Int.dpToPx() = (this * resources.displayMetrics.density).toInt()

        // Puxa a fonte "SF Pro" diretamente da pasta res/font
        val fonteSfPro = ResourcesCompat.getFont(this, R.font.sf_pro)

        // Loop para desenhar os cards dinamicamente
        for (item in lista) {
            // 1. Card principal branco com borda suave na cor da EcoSmile
            val cardView = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16.dpToPx()) }
                radius = 20.dpToPx().toFloat()
                strokeWidth = 2.dpToPx()
                setStrokeColor(Color.parseColor("#FFD4C4"))
                cardElevation = 0f
                setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            val layoutItem = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24.dpToPx(), 24.dpToPx(), 24.dpToPx(), 24.dpToPx())
            }

            // 2. Título do prêmio (SF Pro Bold)
            val txtTitulo = TextView(this).apply {
                text = item.titulo
                textSize = 16f
                setTypeface(fonteSfPro, Typeface.BOLD)
                setTextColor(Color.parseColor("#111111"))
            }

            // 3. Descrição (SF Pro normal)
            val txtDesc = TextView(this).apply {
                text = item.descricao
                textSize = 14f
                typeface = fonteSfPro
                setTextColor(Color.parseColor("#777777"))
                setPadding(0, 4.dpToPx(), 0, 16.dpToPx())
            }

            // 4. Badge de fundo laranja suave
            val badgeCupom = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                radius = 8.dpToPx().toFloat()
                cardElevation = 0f
                strokeWidth = 1.dpToPx()
                setStrokeColor(Color.parseColor("#FFD4C4"))
                setCardBackgroundColor(Color.parseColor("#FFECE6"))
            }

            // 5. Texto do cupom (laranja da marca, SF Pro Bold)
            val txtCupom = TextView(this).apply {
                text = item.codigoCupom
                textSize = 14f
                setTypeface(fonteSfPro, Typeface.BOLD)
                setTextColor(Color.parseColor("#F97553"))
                setPadding(12.dpToPx(), 6.dpToPx(), 12.dpToPx(), 6.dpToPx())
            }

            badgeCupom.addView(txtCupom)

            // 6. Data de resgate (SF Pro normal)
            val txtData = TextView(this).apply {
                text = "Resgatado em: ${item.dataResgate}"
                textSize = 12f
                typeface = fonteSfPro
                setTextColor(Color.parseColor("#B0B0B0"))
                setPadding(0, 16.dpToPx(), 0, 0)
            }

            // Montagem do card
            layoutItem.addView(txtTitulo)
            layoutItem.addView(txtDesc)
            layoutItem.addView(badgeCupom)
            layoutItem.addView(txtData)

            cardView.addView(layoutItem)
            containerDescontos.addView(cardView)
        }
    }
}
