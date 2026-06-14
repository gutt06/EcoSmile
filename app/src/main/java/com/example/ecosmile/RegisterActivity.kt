package com.example.ecosmile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        val etNome = findViewById<EditText>(R.id.registerName)
        val etEmail = findViewById<EditText>(R.id.registerEmail)
        val etSenha = findViewById<EditText>(R.id.registerPassword)
        val etConfirmarSenha = findViewById<EditText>(R.id.confirmarSenhaEditText)
        btnCadastrar = findViewById(R.id.registerButton)
        val voltarLoginLayout = findViewById<LinearLayout>(R.id.voltarLoginLinearLayout)

        btnBack.setOnClickListener { finish() }
        voltarLoginLayout.setOnClickListener { finish() }

        btnCadastrar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString()
            val confirmarSenha = etConfirmarSenha.text.toString()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cadastrarNoServidor(nome, email, senha)
        }

    }

    private fun cadastrarNoServidor(nome: String, email: String, senha: String) {
        // Indica visualmente que a conta está sendo criada e evita cliques duplicados
        btnCadastrar.isEnabled = false
        btnCadastrar.text = "Criando conta..."

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.cadastrar(nome, email, senha, null)

        call.enqueue(object : Callback<UnificadoResponse> {
            override fun onResponse(call: Call<UnificadoResponse>, response: Response<UnificadoResponse>) {
                val body = response.body()
                if (response.isSuccessful && body?.sucesso == true) {
                    // Guarda os dados de sessão e já faz o login automático
                    LoginActivity.idUsuarioLogado = body.usuarioId ?: 1
                    LoginActivity.codigoPacienteLogado = body.codigoPaciente ?: "NDCW"

                    Toast.makeText(
                        this@RegisterActivity,
                        "Conta criada! Seu código é ${body.codigoPaciente}",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.putExtra("NOME_USUARIO", body.usuarioNome)
                    startActivity(intent)
                    finish()
                } else {
                    restaurarBotaoCadastrar()
                    val mensagem = body?.mensagem ?: "Erro ao cadastrar."
                    Toast.makeText(this@RegisterActivity, mensagem, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UnificadoResponse>, t: Throwable) {
                restaurarBotaoCadastrar()
                Toast.makeText(this@RegisterActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun restaurarBotaoCadastrar() {
        btnCadastrar.isEnabled = true
        btnCadastrar.text = "Cadastrar"
    }

}