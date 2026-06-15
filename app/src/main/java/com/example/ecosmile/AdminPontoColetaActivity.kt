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

class AdminPontoColetaActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEndereco: EditText
    private lateinit var etHorario: EditText
    private lateinit var btnCriar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_admin_ponto_coleta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageView>(R.id.backButton)
        etNome = findViewById(R.id.etNome)
        etEndereco = findViewById(R.id.etEndereco)
        etHorario = findViewById(R.id.etHorario)
        btnCriar = findViewById(R.id.btnCriar)

        btnBack.setOnClickListener { finish() }

        btnCriar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val endereco = etEndereco.text.toString().trim()
            val horario = etHorario.text.toString().trim()

            if (nome.isEmpty() || endereco.isEmpty() || horario.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            criarPontoColeta(nome, endereco, horario)
        }
    }

    private fun criarPontoColeta(nome: String, endereco: String, horario: String) {
        // Indica visualmente que o ponto de coleta está sendo cadastrado e evita cliques duplicados
        btnCriar.isEnabled = false
        btnCriar.text = "Criando..."

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.criarPontoColeta(nome, endereco, horario).enqueue(object : Callback<MensagemResponse> {
            override fun onResponse(call: Call<MensagemResponse>, response: Response<MensagemResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.sucesso == true) {
                    Toast.makeText(this@AdminPontoColetaActivity, body.mensagem ?: "Ponto de coleta criado!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    restaurarBotao()
                    Toast.makeText(this@AdminPontoColetaActivity, body?.mensagem ?: "Erro ao criar ponto de coleta.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MensagemResponse>, t: Throwable) {
                restaurarBotao()
                Toast.makeText(this@AdminPontoColetaActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun restaurarBotao() {
        btnCriar.isEnabled = true
        btnCriar.text = "Criar Local"
    }
}
