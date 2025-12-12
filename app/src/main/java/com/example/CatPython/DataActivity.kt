package com.example.CatPython

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
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

        // Listener del botón continuar sin lambda
        bContinue.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                playButtonSound()

                val userName = etName.text.toString().trim()
                val userAge = etAge.text.toString().toIntOrNull() ?: 0

                if (userName.isNotEmpty() && userAge != 0) {
                    val user = User(userName, userAge)
                    val intent = Intent(this@DataActivity, GameDificulty::class.java)
                    intent.putExtra("user_data", user)
                    startActivity(intent)
                    finish()
                } else {
                    showAlert("Error", "Debes completar nombre y edad.", this@DataActivity)
                }
            }
        })
    }

    private fun startViews() {
        etName = findViewById(R.id.etNombre)
        etAge = findViewById(R.id.etEdad)
        bContinue = findViewById(R.id.bContinue)
    }

    private fun playButtonSound() {
        try {
            val mediaPlayer = MediaPlayer.create(this, R.raw.button)

            mediaPlayer.setOnCompletionListener(object : MediaPlayer.OnCompletionListener {
                override fun onCompletion(mp: MediaPlayer) {
                    mp.release()
                }
            })

            mediaPlayer.setOnErrorListener(object : MediaPlayer.OnErrorListener {
                override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
                    mp.release()
                    return true
                }
            })

            mediaPlayer.start()
        } catch (e: Exception) {
            Log.e("AudioDebug", "Error en sonido: ${e.message}")
        }
    }
}