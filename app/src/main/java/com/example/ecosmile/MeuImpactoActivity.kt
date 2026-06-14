package com.example.ecosmile

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MeuImpactoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Infla o layout completo de impacto com as estatísticas de vasos ecológicos e CO2
        setContentView(R.layout.activity_meu_impacto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnVoltarImpacto = findViewById<ImageView>(R.id.btnVoltarImpacto)

        // Ação de fechar a tela e voltar ao menu anterior
        btnVoltarImpacto.setOnClickListener {
            finish()
        }
    }
}
