package com.example.ecosmile

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminTokenActivity : AppCompatActivity() {

    private lateinit var llTokensGerados: LinearLayout
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_token)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        val btnBack          = findViewById<ImageButton>(R.id.backButton)
        val etTipoDesconto   = findViewById<EditText>(R.id.etTipoDescontoId)
        val btnGerar         = findViewById<Button>(R.id.btnGerar)
        llTokensGerados      = findViewById(R.id.llTokensGerados)

        btnBack.setOnClickListener { finish() }

        // Carrega tokens ao abrir a tela
        carregarTokens()

        btnGerar.setOnClickListener {
            val tipoDescontoId = etTipoDesconto.text.toString().trim()

            if (tipoDescontoId.isEmpty()) {
                Toast.makeText(this, "Informe o tipo de desconto!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            gerarToken(tipoDescontoId)
        }
    }

    private fun gerarToken(tipoDescontoId: String) {
        apiService.gerarToken(tipoDescontoId).enqueue(object : Callback<TokenGeradoResponse> {
            override fun onResponse(call: Call<TokenGeradoResponse>, response: Response<TokenGeradoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val tokenGerado = response.body()!!
                    Toast.makeText(
                        this@AdminTokenActivity,
                        "Token gerado: ${tokenGerado.numero}",
                        Toast.LENGTH_LONG
                    ).show()
                    carregarTokens() // Atualiza a lista
                } else {
                    Toast.makeText(this@AdminTokenActivity, "Erro ao gerar token.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TokenGeradoResponse>, t: Throwable) {
                Toast.makeText(this@AdminTokenActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun carregarTokens() {
        apiService.listarTokens().enqueue(object : Callback<List<TokenResponse>> {
            override fun onResponse(call: Call<List<TokenResponse>>, response: Response<List<TokenResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    exibirTokens(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<TokenResponse>>, t: Throwable) {
                Toast.makeText(this@AdminTokenActivity, "Erro ao carregar tokens.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun exibirTokens(tokens: List<TokenResponse>) {
        llTokensGerados.removeAllViews() // Limpa antes de repopular

        if (tokens.isEmpty()) {
            val tv = TextView(this)
            tv.text = "Nenhum token gerado ainda."
            llTokensGerados.addView(tv)
            return
        }

        tokens.forEach { token ->
            val tv = TextView(this)
            tv.text = "${token.Numero} - ${token.Descricao}"
            tv.setPadding(16, 12, 16, 12)
            tv.setBackgroundResource(R.drawable.bg_token_item)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 12)
            tv.layoutParams = params
            llTokensGerados.addView(tv)
        }
    }
}