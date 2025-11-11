package com.example.myapplication

import android.content.Intent

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class DataActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var bContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data)
        startViews()

        bContinue.setOnClickListener {
            val userName = etName.text.toString().trim()
            val userAge = etAge.text.toString().toIntOrNull() ?: 0

            if (userName.isNotEmpty() && userAge != 0) {
                val user = User(userName, userAge)
                val intent = Intent(this, GameDificulty::class.java)
                intent.putExtra("user_data", user)
                startActivity(intent)
            } else {
                showAlert("Error", "Debes completar nombre y edad.", this)
            }
        }
    }

    private fun startViews() {
        etName = findViewById(R.id.etNombre)
        etAge = findViewById(R.id.etEdad)
        bContinue = findViewById(R.id.bContinue)
    }
}



