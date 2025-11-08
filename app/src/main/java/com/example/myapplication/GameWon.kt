package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameWon : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_won)


        val buttonExit = findViewById<ImageButton>(R.id.button_exit)
        findViewById<ImageButton>(R.id.button_exit).setOnClickListener {
            setupExitButton(this, buttonExit )
        }
    }
}