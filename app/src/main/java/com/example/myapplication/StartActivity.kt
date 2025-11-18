package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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
            playButtonSound()

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

    private fun playButtonSound() {
        try {
            val mediaPlayer = MediaPlayer.create(this, R.raw.button)
            mediaPlayer.setOnCompletionListener { mp ->
                Log.d("AudioDebug", "Sonido de botón completado")
                mp.release() // Liberar recursos cuando termine la reproducción
            }
            mediaPlayer.setOnErrorListener { mp, what, extra ->
                Log.e("AudioDebug", "Error reproduciendo sonido: what=$what, extra=$extra")
                mp.release()
                true
            }
            mediaPlayer.start()
            Log.d("AudioDebug", "Reproduciendo sonido de botón")
        } catch (e: Exception) {
            Log.e("AudioDebug", "Excepción al reproducir sonido: ${e.message}")
        }
    }

    // No necesitamos onDestroy() ya que liberamos el MediaPlayer inmediatamente después de usarlo
}