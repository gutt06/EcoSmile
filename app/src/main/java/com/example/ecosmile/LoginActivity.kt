package com.example.ecosmile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializando os campos de texto e botões
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val registerTextView: TextView = findViewById(R.id.registerTextView)
        val forgotPasswordTextView: TextView = findViewById(R.id.forgotPasswordTextView)

        // 2. Ação do botão de Login
        loginButton.setOnClickListener {
            executarLogin()
        }

        // 3. Ação para abrir a tela de Cadastro
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 4. Ação para abrir a tela de Esqueci a Senha
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    private fun executarLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validação de campos vazios
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Configuração do Retrofit (Certifique-se de usar o seu IP atual)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.login(email, password)

        // 1. Tenta login como Admin primeiro
        apiService.loginAdmin(email, password).enqueue(object : Callback<List<AdminResponse>> {
            override fun onResponse(call: Call<List<AdminResponse>>, response: Response<List<AdminResponse>>) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    // É admin — vai para AdminHomeActivity
                    Toast.makeText(this@LoginActivity, "Bem-vindo, Administrador!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, AdminHomeActivity::class.java))
                    finish()
                } else {
                    // Não é admin — tenta login como usuário comum
                    tentarLoginUsuario(apiService, email, password)
                }
            }

            override fun onFailure(call: Call<List<AdminResponse>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun tentarLoginUsuario(apiService: ApiService, email: String, password: String) {
        apiService.login(email, password).enqueue(object : Callback<List<LoginResponse>> {
            override fun onResponse(call: Call<List<LoginResponse>>, response: Response<List<LoginResponse>>) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    // É usuário comum — vai para MainActivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "E-mail ou senha incorretos", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<LoginResponse>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}