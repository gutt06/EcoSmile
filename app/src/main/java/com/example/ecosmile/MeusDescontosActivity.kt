package com.example.ecosmile

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class MeusDescontosActivity : AppCompatActivity() {

    private lateinit var cardVazioDescontos: MaterialCardView
    private lateinit var containerDescontos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meus_descontos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cardVazioDescontos = findViewById(R.id.cardVazioDescontos)
        containerDescontos = findViewById(R.id.containerDescontos)
        val btnVoltar = findViewById<ImageView>(R.id.btnVoltarDescontos)

        btnVoltar.setOnClickListener { finish() }

        renderizarCupons()
    }

    private fun renderizarCupons() {
        val lista = EcoSmileActivity.listaDescontos

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

        // Loop para desenhar os cards dinamicamente
        for (item in lista) {
            val cardView = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 24) }
                radius = 40f
                strokeWidth = 3
                setStrokeColor(Color.parseColor("#E5F4EC")) // Borda verde clara para remeter a "sucesso/resgatado"
                cardElevation = 0f
                setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            val layoutItem = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(48, 48, 48, 48)
            }

            val txtTitulo = TextView(this).apply {
                text = item.titulo
                textSize = 18f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#111111"))
            }

            val txtDesc = TextView(this).apply {
                text = item.descricao
                textSize = 14f
                setTextColor(Color.parseColor("#777777"))
                setPadding(0, 8, 0, 24)
            }

            // Bloco destacado do Cupom
            val badgeCupom = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                radius = 16f
                cardElevation = 0f
                setCardBackgroundColor(Color.parseColor("#F0F9F5")) // Fundo verde clarinho
            }

            val txtCupom = TextView(this).apply {
                text = item.codigoCupom
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#3A875D")) // Texto Verde Escuro
                setPadding(32, 16, 32, 16)
            }

            badgeCupom.addView(txtCupom)

            val txtData = TextView(this).apply {
                text = "Resgatado em: ${item.dataResgate}"
                textSize = 12f
                setTextColor(Color.parseColor("#BBBBBB"))
                setPadding(0, 24, 0, 0)
            }

            layoutItem.addView(txtTitulo)
            layoutItem.addView(txtDesc)
            layoutItem.addView(badgeCupom)
            layoutItem.addView(txtData)

            cardView.addView(layoutItem)
            containerDescontos.addView(cardView)
        }
    }
}
