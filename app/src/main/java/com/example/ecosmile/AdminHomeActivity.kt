package com.example.ecosmile

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // Botão Cadastrar Tipo de Consulta
        findViewById<LinearLayout>(R.id.btnTipoConsulta).setOnClickListener {
            startActivity(Intent(this, AdminTipoConsultaActivity::class.java))
        }

        // Botão Cadastrar Desconto
        findViewById<LinearLayout>(R.id.btnTipoDesconto).setOnClickListener {
            startActivity(Intent(this, AdminTipoDescontoActivity::class.java))
        }

        // Botão Cadastrar Ponto de Coleta
        findViewById<LinearLayout>(R.id.btnPontoColeta).setOnClickListener {
            startActivity(Intent(this, AdminPontoColetaActivity::class.java))
        }

        // Botão Cadastrar Token
        findViewById<LinearLayout>(R.id.btnToken).setOnClickListener {
            startActivity(Intent(this, AdminTokenActivity::class.java))
        }

        // Botão Sair
        findViewById<TextView>(R.id.btnSair).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity() // Fecha todas as activities
        }

    }
}