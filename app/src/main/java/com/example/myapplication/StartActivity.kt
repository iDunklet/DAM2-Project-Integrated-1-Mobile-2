package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)

        val btnPlay = findViewById<ImageButton>(R.id.playButton)
        val clickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_play_click)
        val releaseAnimation = AnimationUtils.loadAnimation(this, R.anim.button_play_release)
        val btnSecret = findViewById<Button>(R.id.bSecret)


        btnSecret.setOnClickListener {
            showPasswordDialog()
        }
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
    private fun showPasswordDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter password")

        val input = android.widget.EditText(this)
        input.hint = "Password"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD

        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val password = input.text.toString()

            if (password == "231204") {
                // Correct password → go to DataActivity

                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } else {
                // Wrong password
                android.widget.Toast.makeText(this, "Incorrect password", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}