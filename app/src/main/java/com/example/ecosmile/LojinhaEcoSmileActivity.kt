package com.example.ecosmile

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LojinhaEcoSmileActivity : AppCompatActivity() {

    private val urlApi = "https://api-ecosmile.onrender.com/"

    private lateinit var txtSaldoLojinha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lojinha_ecosmile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtSaldoLojinha = findViewById(R.id.txtSaldoLojinha)
        val btnVoltar = findViewById<ImageView>(R.id.btnVoltarLojinha)

        val btnResgatarDesconto = findViewById<Button>(R.id.btnResgatarDesconto)
        val btnResgatarEscova = findViewById<Button>(R.id.btnResgatarEscova)

        // Traz o saldo real do usuário logo que a tela abre
        buscarSaldoReal()

        btnVoltar.setOnClickListener { finish() }

        // Conecta os botões com os novos valores super acessíveis
        btnResgatarDesconto.setOnClickListener {
            tentarResgatarProduto(btnResgatarDesconto, "Desconto de 15%", "Aplicável em manutenções", "DESC15", 10)
        }

        btnResgatarEscova.setOnClickListener {
            tentarResgatarProduto(btnResgatarEscova, "Escova Ecológica Bamboo", "Brinde exclusivo sustentável", "BAMBOO", 15)
        }
    }

    private fun buscarSaldoReal() {
        // Indica visualmente que o saldo está sendo carregado
        txtSaldoLojinha.text = "Carregando..."

        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.buscarSaldo(LoginActivity.idUsuarioLogado).enqueue(object : Callback<SaldoResponse> {
            override fun onResponse(call: Call<SaldoResponse>, response: Response<SaldoResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    txtSaldoLojinha.text = "${response.body()!!.saldo} pts"
                } else {
                    txtSaldoLojinha.text = "0 pts"
                }
            }

            override fun onFailure(call: Call<SaldoResponse>, t: Throwable) {
                txtSaldoLojinha.text = "0 pts"
                Toast.makeText(this@LojinhaEcoSmileActivity, "Erro de conexão ao buscar saldo.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // O Motor de Compra (Conectado ao banco de dados)
    private fun tentarResgatarProduto(botao: Button, titulo: String, descricao: String, prefixoCupom: String, custoPontos: Int) {
        // Indica visualmente que o resgate está sendo processado e evita cliques duplicados
        val textoOriginalBotao = botao.text
        botao.isEnabled = false
        botao.text = "Processando..."

        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.resgatarCupom(
            LoginActivity.idUsuarioLogado,
            custoPontos,
            titulo,
            descricao,
            prefixoCupom
        ).enqueue(object : Callback<ResgateResponse> {
            override fun onResponse(call: Call<ResgateResponse>, response: Response<ResgateResponse>) {
                botao.isEnabled = true
                botao.text = textoOriginalBotao

                val body = response.body()
                if (response.isSuccessful && body?.sucesso == true) {
                    // Atualiza o saldo exibido com o valor já descontado pelo servidor
                    buscarSaldoReal()
                    mostrarPopUpSucesso(titulo, body.codigoCupom ?: "")
                } else {
                    val mensagem = body?.mensagem ?: "Não foi possível resgatar este item."
                    Toast.makeText(this@LojinhaEcoSmileActivity, mensagem, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResgateResponse>, t: Throwable) {
                botao.isEnabled = true
                botao.text = textoOriginalBotao
                Toast.makeText(this@LojinhaEcoSmileActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun mostrarPopUpSucesso(nomeProduto: String, cupom: String) {
        // Usa o layout Premium de Pop-up que criamos no começo
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val message = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val button = dialogView.findViewById<TextView>(R.id.dialogButton)

        title.text = "Resgate Concluído!"
        message.text = "Você adquiriu: $nomeProduto.\n\nCupom gerado: $cupom\n\nEle já foi salvo na sua aba 'Meus Descontos'!"
        button.text = "VER MINHA CARTEIRA"

        button.setOnClickListener {
            dialog.dismiss()
            finish() // Fecha a lojinha e volta pra tela principal para a pessoa ir ver o desconto
        }

        dialog.show()
    }
}
