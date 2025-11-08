package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.RadioButton
import android.widget.RadioGroup

class DataActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEdad: EditText
    private lateinit var rgDificultad: RadioGroup
    private lateinit var rbEasy: RadioButton
    private lateinit var rbHard: RadioButton
    private lateinit var bContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data)

        startViews()
        setupRadioButtonDificulty()

    }



    private fun startViews() {
        etNombre = findViewById(R.id.etNombre)
        etEdad = findViewById(R.id.etEdad)
        rgDificultad = findViewById(R.id.rbEasyHard)
        rbEasy = findViewById(R.id.rbEasy)
        rbHard = findViewById(R.id.rbHard)
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


