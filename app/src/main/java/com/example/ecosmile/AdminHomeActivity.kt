package com.example.ecosmile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class AdminHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Card "Configurar Pontos" -> Regra de pontos por alinhador devolvido
        findViewById<MaterialCardView>(R.id.btnConfigurarPontos).setOnClickListener {
            startActivity(Intent(this, AdminConfigurarPontosActivity::class.java))
        }

        // Card "Cadastrar desconto" -> cria item tipo "desconto" na Lojinha
        findViewById<MaterialCardView>(R.id.btnCadastrarDesconto).setOnClickListener {
            startActivity(Intent(this, AdminTipoDescontoActivity::class.java))
        }

        // Card "Cadastrar ponto de coleta" -> novo ponto de coleta
        findViewById<MaterialCardView>(R.id.btnCadastrarPontoColeta).setOnClickListener {
            startActivity(Intent(this, AdminPontoColetaActivity::class.java))
        }

        // Card "Cadastrar produto físico" -> cria item tipo "produto" na Lojinha
        findViewById<MaterialCardView>(R.id.btnCadastrarProduto).setOnClickListener {
            startActivity(Intent(this, AdminProdutoFisicoActivity::class.java))
        }

        // Botão Sair
        findViewById<LinearLayout>(R.id.btnSair).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity() // Fecha todas as activities
        }

    }
}
