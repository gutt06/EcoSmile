package com.example.ecosmile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val boasVindasTextView = findViewById<TextView>(R.id.boasVindasNomeTextView)
        val txtCodigoPaciente = findViewById<TextView>(R.id.txtCodigoPaciente)
        val txtFaseGrande = findViewById<TextView>(R.id.txtFaseGrande)
        val txtFaseDescricao = findViewById<TextView>(R.id.txtFaseDescricao)
        val progressFaseCircular = findViewById<ProgressBar>(R.id.progressFaseCircular)
        val txtPorcentagem = findViewById<TextView>(R.id.txtPorcentagem)
        val cardEcoSmile = findViewById<MaterialCardView>(R.id.cardEcoSmile)
        val cardDescontos = findViewById<MaterialCardView>(R.id.cardDescontos)
        val btnSair = findViewById<LinearLayout>(R.id.btnSair)

        // 1. Recebe o nome do usuário passado pelo LoginActivity
        val nomeUsuario = intent.getStringExtra("NOME_USUARIO") ?: ""
        val primeiroNome = if (nomeUsuario.isNotEmpty()) nomeUsuario.split(" ")[0] else ""
        boasVindasTextView.text = primeiroNome

        // 2. Código fixo do paciente
        txtCodigoPaciente.text = "NDCW"

        // 3. Dados de progresso
        val faseAtual = 3
        val totalFases = 17
        val porcentagemCalculada = Math.round((faseAtual.toDouble() * 100) / totalFases).toInt()

        txtFaseGrande.text = "$faseAtual/$totalFases"
        txtFaseDescricao.text = "essa é a fase $faseAtual de $totalFases\nda sua jornada"
        progressFaseCircular.progress = porcentagemCalculada
        txtPorcentagem.text = "$porcentagemCalculada%"

        // 4. Navegação
        cardEcoSmile.setOnClickListener {
            startActivity(Intent(this, EcoSmileActivity::class.java))
        }

        cardDescontos.setOnClickListener {
            startActivity(Intent(this, MeusDescontosActivity::class.java))
        }

        btnSair.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }
}
