package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)

        val btnPlay = findViewById<ImageButton>(R.id.playButton)

        btnPlay.setOnClickListener {
            val intent = Intent(this, DataActivity::class.java)
            startActivity(intent)
        }


    }

}