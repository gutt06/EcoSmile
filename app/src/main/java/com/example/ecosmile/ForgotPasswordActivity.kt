package com.example.ecosmile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

class ForgotPasswordActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.backButtonForgot)
        val etEmail = findViewById<EditText>(R.id.forgotEmail)
        val etNovaSenha = findViewById<EditText>(R.id.newPassword)
        val btnUpdate = findViewById<Button>(R.id.btnUpdatePassword)
        val voltarLoginTextView = findViewById<TextView>(R.id.voltarLoginTextView)

        btnBack.setOnClickListener { finish() }
        voltarLoginTextView.setOnClickListener { finish() }

        btnUpdate.setOnClickListener {
            val email = etEmail.text.toString()
            val novaSenha = etNovaSenha.text.toString()

            if (email.isEmpty() || novaSenha.isEmpty()) {
                Toast.makeText(this, "Preencha os campos!", Toast.LENGTH_SHORT).show()
            } else {
                atualizarSenhaNoServidor(email, novaSenha)
            }
        }

    }

    private fun atualizarSenhaNoServidor(email: String, novaSenha: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com") //IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.trocarSenha(email, novaSenha)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ForgotPasswordActivity, "Senha alterada!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Erro ao alterar.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ForgotPasswordActivity, "Erro de conexão.", Toast.LENGTH_SHORT).show()
            }
        })
    }

}