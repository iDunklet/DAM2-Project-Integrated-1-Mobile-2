package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameWon : AppCompatActivity() {

    private lateinit var bExit: ImageButton
    private lateinit var bTryAgain: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_won)
        startViews()
        //user fake
        val fakeUser = User(
            name = listOf("Carlos", "María", "Lucía", "Juan", "Sofía", "Diego").random(),
            age = (10..60).random(),
            gameList = mutableListOf(
                UserDataGame(
                    gameTime = (30..300).random(),
                    errors = (0..10).random(),
                    points = (100..1000).random(),
                    dificulty = listOf("Fácil", "Medio", "Difícil").random()
                ),
                UserDataGame(
                    gameTime = (30..300).random(),
                    errors = (0..10).random(),
                    points = (100..1000).random(),
                    dificulty = listOf("Fácil", "Medio", "Difícil").random()
                )
            )
        )



        bExit.setOnClickListener {
            setupExitButton(fakeUser, this)
        }
        bTryAgain.setOnClickListener {
            setupTryAgainButton(fakeUser,this)
        }


    }

    private fun startViews() {
        bExit = findViewById<ImageButton>(R.id.button_exit)
        bTryAgain = findViewById<ImageButton>(R.id.button_try_again)
    }
}