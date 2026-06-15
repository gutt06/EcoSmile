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

class AdminProdutoFisicoActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etDescricao: EditText
    private lateinit var etCustoPontos: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_admin_produto_fisico)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.backButton)
        etNome = findViewById(R.id.etNome)
        etDescricao = findViewById(R.id.etDescricao)
        etCustoPontos = findViewById(R.id.etCustoPontos)
        btnCadastrar = findViewById(R.id.btnCadastrar)

        btnBack.setOnClickListener { finish() }

        btnCadastrar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val descricao = etDescricao.text.toString().trim()
            val custoPontosTexto = etCustoPontos.text.toString().trim()

            if (nome.isEmpty() || descricao.isEmpty() || custoPontosTexto.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val custoPontos = custoPontosTexto.toIntOrNull()

            if (custoPontos == null || custoPontos < 0) {
                Toast.makeText(this, "Informe um custo em pontos válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            criarProduto(nome, descricao, custoPontos)
        }
    }

    private fun criarProduto(nome: String, descricao: String, custoPontos: Int) {
        // Indica visualmente que o produto está sendo cadastrado e evita cliques duplicados
        btnCadastrar.isEnabled = false
        btnCadastrar.text = "Cadastrando..."

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.criarProdutoLojinha(
            tipo = "produto",
            titulo = nome,
            descricao = descricao,
            valorDesconto = null,
            custoPontos = custoPontos
        ).enqueue(object : Callback<MensagemResponse> {
            override fun onResponse(call: Call<MensagemResponse>, response: Response<MensagemResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.sucesso == true) {
                    Toast.makeText(this@AdminProdutoFisicoActivity, body.mensagem ?: "Produto adicionado à Lojinha!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    restaurarBotao()
                    Toast.makeText(this@AdminProdutoFisicoActivity, body?.mensagem ?: "Erro ao criar produto.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MensagemResponse>, t: Throwable) {
                restaurarBotao()
                Toast.makeText(this@AdminProdutoFisicoActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun restaurarBotao() {
        btnCadastrar.isEnabled = true
        btnCadastrar.text = "Cadastrar Produto"
    }
}
