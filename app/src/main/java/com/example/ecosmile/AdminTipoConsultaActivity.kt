package com.example.ecosmile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminTipoConsultaActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_tipo_consulta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack    = findViewById<ImageButton>(R.id.backButton)
        val etDescricao = findViewById<EditText>(R.id.etDescricao)
        val etUnidade  = findViewById<EditText>(R.id.etUnidade)
        val etValor    = findViewById<EditText>(R.id.etValor)
        val btnCriar   = findViewById<Button>(R.id.btnCriar)

        btnBack.setOnClickListener { finish() }

        btnCriar.setOnClickListener {
            val descricao = etDescricao.text.toString().trim()
            val unidade   = etUnidade.text.toString().trim()
            val valor     = etValor.text.toString().trim()

            if (descricao.isEmpty() || unidade.isEmpty() || valor.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            criarTipoConsulta(descricao, unidade, valor)
        }
    }

    private fun criarTipoConsulta(descricao: String, unidade: String, valor: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.criarTipoConsulta(descricao, unidade, valor).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AdminTipoConsultaActivity, "Tipo de consulta criado!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@AdminTipoConsultaActivity, "Erro ao criar.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AdminTipoConsultaActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })

    }
}