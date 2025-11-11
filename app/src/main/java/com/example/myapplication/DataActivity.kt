package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup
import kotlin.toString

class DataActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAge: EditText

    private lateinit var bContinue: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data)
        startViews()
        //setupRadioButtonDificulty()




        bContinue.setOnClickListener {
            val userName = etName.text.toString().trim()
            val userAge = etAge.text.toString().toIntOrNull() ?: 0

            if (userName.isNotEmpty() && userAge != 0) {
                val user = User(userName, userAge)

                val intent = Intent(this, GameDificulty::class.java)
                intent.putExtra("user_data", user)
                startActivity(intent)

            } else {
                    showAlert("Error", "Debes completar nombre y edad.",this)
            }
        }

    }



    private fun startViews() {
        etName = findViewById(R.id.etNombre)
        etAge = findViewById(R.id.etEdad)
        bContinue = findViewById(R.id.bContinue)
    }

    private fun setupRadioButtonDificulty() {
        val rg = findViewById<RadioGroup>(R.id.rbEasyHard)
        val reasy = findViewById<RadioButton>(R.id.rbEasy)
        val rHard = findViewById<RadioButton>(R.id.rbHard)

        rg.setOnCheckedChangeListener { group, checkedId ->
            // Resetear colores de texto a gris
            reasy.setTextColor(Color.parseColor("#666666"))
            rHard.setTextColor(Color.parseColor("#666666"))

            // Cambiar color del texto seleccionado a blanco
            when (checkedId) {
                R.id.rbEasy -> reasy.setTextColor(Color.WHITE)
                R.id.rbHard -> rHard.setTextColor(Color.WHITE)
            }
        }
    }
}


