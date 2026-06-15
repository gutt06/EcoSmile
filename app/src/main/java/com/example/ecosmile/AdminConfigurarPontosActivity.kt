package com.example.ecosmile

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminConfigurarPontosActivity : AppCompatActivity() {

    private val urlApi = "https://api-ecosmile.onrender.com/"
    private val valorMinimo = 0
    private val valorMaximo = 500

    private var valorAtual = 50
    private var carregando = true

    private lateinit var txtValorPontos: TextView
    private lateinit var btnDecrementar: MaterialCardView
    private lateinit var btnIncrementar: MaterialCardView
    private lateinit var btnSalvar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_configurar_pontos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.backButton)
        txtValorPontos = findViewById(R.id.txtValorPontos)
        btnDecrementar = findViewById(R.id.btnDecrementar)
        btnIncrementar = findViewById(R.id.btnIncrementar)
        btnSalvar = findViewById(R.id.btnSalvarConfig)

        btnBack.setOnClickListener { finish() }

        btnDecrementar.setOnClickListener {
            if (!carregando && valorAtual > valorMinimo) {
                valorAtual--
                atualizarValorExibido()
            }
        }

        btnIncrementar.setOnClickListener {
            if (!carregando && valorAtual < valorMaximo) {
                valorAtual++
                atualizarValorExibido()
            }
        }

        btnSalvar.setOnClickListener {
            salvarConfiguracao()
        }

        carregarConfiguracaoAtual()
    }

    private fun atualizarValorExibido() {
        txtValorPontos.text = valorAtual.toString()
    }

    private fun carregarConfiguracaoAtual() {
        // Indica visualmente que a configuração está sendo carregada
        carregando = true
        txtValorPontos.text = "Carregando..."
        btnSalvar.isEnabled = false

        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.buscarConfig().enqueue(object : Callback<ConfigResponse> {
            override fun onResponse(call: Call<ConfigResponse>, response: Response<ConfigResponse>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    valorAtual = body.pontosPorAlinhador
                } else {
                    valorAtual = 50
                    Toast.makeText(this@AdminConfigurarPontosActivity, "Não foi possível carregar a configuração atual.", Toast.LENGTH_SHORT).show()
                }
                carregando = false
                btnSalvar.isEnabled = true
                atualizarValorExibido()
            }

            override fun onFailure(call: Call<ConfigResponse>, t: Throwable) {
                valorAtual = 50
                carregando = false
                btnSalvar.isEnabled = true
                atualizarValorExibido()
                Toast.makeText(this@AdminConfigurarPontosActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun salvarConfiguracao() {
        // Indica visualmente que a configuração está sendo salva e evita cliques duplicados
        btnSalvar.isEnabled = false
        btnSalvar.text = "Salvando..."

        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.salvarConfig(valorAtual).enqueue(object : Callback<ConfigResponse> {
            override fun onResponse(call: Call<ConfigResponse>, response: Response<ConfigResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.sucesso == true) {
                    Toast.makeText(this@AdminConfigurarPontosActivity, body.mensagem ?: "Configuração salva!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    restaurarBotaoSalvar()
                    Toast.makeText(this@AdminConfigurarPontosActivity, body?.mensagem ?: "Erro ao salvar configuração.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ConfigResponse>, t: Throwable) {
                restaurarBotaoSalvar()
                Toast.makeText(this@AdminConfigurarPontosActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun restaurarBotaoSalvar() {
        btnSalvar.isEnabled = true
        btnSalvar.text = "Salvar Configuração"
    }
}
