package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)

        val btnPlay = findViewById<ImageButton>(R.id.playButton)
        val clickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_play_click)
        val releaseAnimation = AnimationUtils.loadAnimation(this, R.anim.button_play_release)
        btnPlay.setOnClickListener {
            // Animación de presionado
            btnPlay.startAnimation(clickAnimation)

            // Animación de liberación después de un delay
            btnPlay.postDelayed({
                btnPlay.startAnimation(releaseAnimation)

                // Cambiar a la siguiente actividad después de la animación
                btnPlay.postDelayed({
                    val intent = Intent(this, DataActivity::class.java)
                    startActivity(intent)
                    // Transición suave entre actividades
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 200)
            }, 150)
        }
    }
}
