package com.example.ecosmile

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LojinhaEcoSmileActivity : AppCompatActivity() {

    private val urlApi = "https://api-ecosmile.onrender.com/"

    private lateinit var txtSaldoLojinha: TextView
    private lateinit var cardLojinhaCarregando: MaterialCardView
    private lateinit var cardLojinhaVazio: MaterialCardView
    private lateinit var containerLojinha: LinearLayout

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
        cardLojinhaCarregando = findViewById(R.id.cardLojinhaCarregando)
        cardLojinhaVazio = findViewById(R.id.cardLojinhaVazio)
        containerLojinha = findViewById(R.id.containerLojinha)
        val btnVoltar = findViewById<ImageView>(R.id.btnVoltarLojinha)

        // Traz o saldo real do usuário logo que a tela abre
        buscarSaldoReal()

        // Carrega os itens (descontos e produtos) cadastrados pelo admin
        buscarItensDaLojinha()

        btnVoltar.setOnClickListener { finish() }
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

    private fun buscarItensDaLojinha() {
        // Indica visualmente que os itens estão sendo carregados
        cardLojinhaCarregando.visibility = View.VISIBLE
        cardLojinhaVazio.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl(urlApi)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.buscarProdutosLojinha().enqueue(object : Callback<List<ProdutoLojinhaResponse>> {
            override fun onResponse(call: Call<List<ProdutoLojinhaResponse>>, response: Response<List<ProdutoLojinhaResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    desenharItensNaTela(response.body()!!)
                } else {
                    desenharItensNaTela(emptyList())
                }
            }

            override fun onFailure(call: Call<List<ProdutoLojinhaResponse>>, t: Throwable) {
                Toast.makeText(this@LojinhaEcoSmileActivity, "Erro ao buscar itens da lojinha: ${t.message}", Toast.LENGTH_SHORT).show()
                desenharItensNaTela(emptyList())
            }
        })
    }

    private fun desenharItensNaTela(lista: List<ProdutoLojinhaResponse>) {
        // Os dados chegaram (ou falharam): esconde o indicador de carregamento
        cardLojinhaCarregando.visibility = View.GONE

        // Controle do Estado Vazio
        if (lista.isEmpty()) {
            cardLojinhaVazio.visibility = View.VISIBLE
            containerLojinha.visibility = View.GONE
            return
        } else {
            cardLojinhaVazio.visibility = View.GONE
            containerLojinha.visibility = View.VISIBLE
        }

        containerLojinha.removeAllViews()

        // Função para converter medidas e ficar perfeito em qualquer tela
        fun Int.dpToPx() = (this * resources.displayMetrics.density).toInt()

        // Puxa a fonte "SF Pro" diretamente da pasta res/font
        val fonteSfPro = ResourcesCompat.getFont(this, R.font.sf_pro)

        for (item in lista) {
            val cardView = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 16.dpToPx()) }
                radius = 24.dpToPx().toFloat()
                strokeWidth = (1.5 * resources.displayMetrics.density).toInt()
                setStrokeColor(Color.parseColor("#F0EFEC"))
                cardElevation = 0f
                setCardBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            val layoutItem = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(20.dpToPx(), 20.dpToPx(), 20.dpToPx(), 20.dpToPx())
            }

            val txtTitulo = TextView(this).apply {
                text = item.titulo
                textSize = 18f
                setTypeface(fonteSfPro, Typeface.BOLD)
                setTextColor(Color.parseColor("#111111"))
            }

            val txtDesc = TextView(this).apply {
                text = item.descricao
                textSize = 14f
                typeface = fonteSfPro
                setTextColor(Color.parseColor("#777777"))
                setPadding(0, 4.dpToPx(), 0, 16.dpToPx())
            }

            val btnResgatar = Button(this).apply {
                text = "Resgatar por ${item.custoPontos} pts"
                isAllCaps = false
                setTypeface(fonteSfPro, Typeface.BOLD)
                setTextColor(Color.parseColor("#FFFFFF"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    50.dpToPx()
                )
                background = ResourcesCompat.getDrawable(resources, R.drawable.bg_button_lojinha, theme)
                elevation = 0f
            }

            btnResgatar.setOnClickListener {
                tentarResgatarProduto(btnResgatar, item.produtoId, item.titulo)
            }

            layoutItem.addView(txtTitulo)
            layoutItem.addView(txtDesc)
            layoutItem.addView(btnResgatar)

            cardView.addView(layoutItem)
            containerLojinha.addView(cardView)
        }
    }

    // O Motor de Compra (Conectado ao banco de dados)
    private fun tentarResgatarProduto(botao: Button, produtoId: Int, titulo: String) {
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
            produtoId
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
