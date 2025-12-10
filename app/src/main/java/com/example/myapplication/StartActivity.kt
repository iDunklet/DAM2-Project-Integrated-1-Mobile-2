package com.example.myapplication

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {

    private lateinit var btnPlay: ImageButton
    private lateinit var btnSecret: Button
    private lateinit var clickAnimation: android.view.animation.Animation
    private lateinit var releaseAnimation: android.view.animation.Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)

        // Inicialización de vistas
        btnPlay = findViewById(R.id.playButton)
        btnSecret = findViewById(R.id.bSecret)

        // Animaciones
        clickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_play_click)
        releaseAnimation = AnimationUtils.loadAnimation(this, R.anim.button_play_release)

        // Listener del botón secreto
        btnSecret.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                showPasswordDialog()
            }
        })

        // Listener del botón play
        btnPlay.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                btnPlay.startAnimation(clickAnimation)
                playButtonSound()

                // Animación de liberación después de un delay
                btnPlay.postDelayed(object : Runnable {
                    override fun run() {
                        btnPlay.startAnimation(releaseAnimation)

                        // Cambiar a la siguiente actividad después de otro delay
                        btnPlay.postDelayed(object : Runnable {
                            override fun run() {
                                val intent = Intent(this@StartActivity, DataActivity::class.java)
                                startActivity(intent)
                                overridePendingTransition(
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )
                            }
                        }, 200)
                    }
                }, 150)
            }
        })
    }

    private fun playButtonSound() {
        try {
            val mediaPlayer = MediaPlayer.create(this, R.raw.button)

            mediaPlayer.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
                override fun onCompletion(mp: MediaPlayer) {
                    Log.d("AudioDebug", "Sonido de botón completado")
                    mp.release()
                }
            })

            mediaPlayer.setOnErrorListener(object : MediaPlayer.OnErrorListener {
                override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
                    Log.e("AudioDebug", "Error reproduciendo sonido: what=$what, extra=$extra")
                    mp.release()
                    return true
                }
            })

            mediaPlayer.start()
            Log.d("AudioDebug", "Reproduciendo sonido de botón")
        } catch (e: Exception) {
            Log.e("AudioDebug", "Excepción al reproducir sonido: ${e.message}")
        }
    }

    private fun showPasswordDialog() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter password")

        val input = android.widget.EditText(this).apply {
            hint = "Password"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        }

        builder.setView(input)

        builder.setPositiveButton("OK",
            object : android.content.DialogInterface.OnClickListener {
                override fun onClick(dialog: android.content.DialogInterface?, which: Int) {
                    val password = input.text.toString()
                    if (password == "231204") {
                        val intent = Intent(this@StartActivity, DataPython::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    } else {
                        android.widget.Toast.makeText(
                            this@StartActivity,
                            "Incorrect password",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })

        builder.setNegativeButton("Cancel",
            object : android.content.DialogInterface.OnClickListener {
                override fun onClick(dialog: android.content.DialogInterface?, which: Int) {
                    dialog?.dismiss()
                }
            })

        builder.show()
    }
}