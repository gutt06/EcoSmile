package com.example.ecosmile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminTipoDescontoActivity : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etDescricao: EditText
    private lateinit var etValorDesconto: EditText
    private lateinit var etCustoPontos: EditText
    private lateinit var btnAdicionar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_admin_tipo_desconto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.backButton)
        etTitulo = findViewById(R.id.etTitulo)
        etDescricao = findViewById(R.id.etDescricao)
        etValorDesconto = findViewById(R.id.etValorDesconto)
        etCustoPontos = findViewById(R.id.etCustoPontos)
        btnAdicionar = findViewById(R.id.btnAdicionar)

        btnBack.setOnClickListener { finish() }

        btnAdicionar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val descricao = etDescricao.text.toString().trim()
            val valorDescontoTexto = etValorDesconto.text.toString().trim()
            val custoPontosTexto = etCustoPontos.text.toString().trim()

            if (titulo.isEmpty() || valorDescontoTexto.isEmpty() || custoPontosTexto.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val valorDesconto = valorDescontoTexto.replace(",", ".").toDoubleOrNull()
            val custoPontos = custoPontosTexto.toIntOrNull()

            if (valorDesconto == null || valorDesconto < 0) {
                Toast.makeText(this, "Informe um valor de desconto válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (custoPontos == null || custoPontos < 0) {
                Toast.makeText(this, "Informe um custo em pontos válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            criarDesconto(titulo, descricao, valorDesconto, custoPontos)
        }
    }

    private fun criarDesconto(titulo: String, descricao: String, valorDesconto: Double, custoPontos: Int) {
        // Indica visualmente que o desconto está sendo cadastrado e evita cliques duplicados
        btnAdicionar.isEnabled = false
        btnAdicionar.text = "Adicionando..."

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.criarProdutoLojinha(
            tipo = "desconto",
            titulo = titulo,
            descricao = descricao.ifEmpty { null },
            valorDesconto = valorDesconto.toString(),
            custoPontos = custoPontos
        ).enqueue(object : Callback<MensagemResponse> {
            override fun onResponse(call: Call<MensagemResponse>, response: Response<MensagemResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.sucesso == true) {
                    Toast.makeText(this@AdminTipoDescontoActivity, body.mensagem ?: "Desconto adicionado à Lojinha!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    restaurarBotao()
                    Toast.makeText(this@AdminTipoDescontoActivity, body?.mensagem ?: "Erro ao criar desconto.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MensagemResponse>, t: Throwable) {
                restaurarBotao()
                Toast.makeText(this@AdminTipoDescontoActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun restaurarBotao() {
        btnAdicionar.isEnabled = true
        btnAdicionar.text = "Adicionar à Lojinha"
    }
}
