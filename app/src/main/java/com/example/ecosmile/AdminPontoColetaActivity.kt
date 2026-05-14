package com.example.ecosmile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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

class AdminPontoColetaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_ponto_coleta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack     = findViewById<ImageButton>(R.id.backButton)
        val etNome      = findViewById<EditText>(R.id.etNome)
        val etEndereco  = findViewById<EditText>(R.id.etEndereco)
        val etLatitude  = findViewById<EditText>(R.id.etLatitude)
        val etLongitude = findViewById<EditText>(R.id.etLongitude)
        val etHorario   = findViewById<EditText>(R.id.etHorario)
        val btnCriar    = findViewById<Button>(R.id.btnCriar)

        btnBack.setOnClickListener { finish() }

        btnCriar.setOnClickListener {
            val nome      = etNome.text.toString().trim()
            val endereco  = etEndereco.text.toString().trim()
            val latitude  = etLatitude.text.toString().trim()
            val longitude = etLongitude.text.toString().trim()
            val horario   = etHorario.text.toString().trim()

            if (nome.isEmpty() || endereco.isEmpty() || latitude.isEmpty() ||
                longitude.isEmpty() || horario.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            criarPontoColeta(nome, endereco, latitude, longitude, horario)
        }
    }

    private fun criarPontoColeta(
        nome: String, endereco: String,
        latitude: String, longitude: String, horario: String
    ) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-ecosmile.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        apiService.criarPontoColeta(nome, endereco, latitude, longitude, horario)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminPontoColetaActivity, "Ponto de coleta criado!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@AdminPontoColetaActivity, "Erro ao criar.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AdminPontoColetaActivity, "Erro de conexão: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}