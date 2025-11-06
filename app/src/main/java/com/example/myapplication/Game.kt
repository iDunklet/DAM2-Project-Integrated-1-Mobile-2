package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Game : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val boton = findViewById<ImageButton>(R.id.butonPausa)
        val overlay = findViewById<View>(R.id.pauseEfect)
        boton.setOnClickListener {
            boton.isSelected = !boton.isSelected
            overlay.visibility = if (boton.isSelected) View.VISIBLE else View.GONE
        }
    }
}
