package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity


class GameDificulty : AppCompatActivity() {
    //private var user: User? = null
    private lateinit var user: User
    private lateinit var rgDificulty: RadioGroup
    private lateinit var rbEasy: RadioButton
    private lateinit var rbHard: RadioButton
    private lateinit var bContinue: Button

    private lateinit var difficulty: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_difficulty)
        startViews()
        if (intent.hasExtra("user_data_try_again")) {
            user = intent.getSerializableExtra("user_data_try_again") as User
        } else {
            user = intent.getSerializableExtra("user_data") as User
        }

        rbEasy.isChecked = true
        difficulty = "easy"

        rgDificulty.setOnCheckedChangeListener (object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                difficulty = if (checkedId == R.id.rbEasy) {
                    "easy"
                } else {
                    "hard"
                }
            }
        })
        bContinue.setOnClickListener {
            bContinue.setOnClickListener {
                if (this::difficulty.isInitialized) {
                    val newGameDificulty = UserDataGame(
                        gameTime = null,
                        errors = null,
                        points = null,
                        dificulty = difficulty
                    )

                    if (user.gameList == null) {
                        user.gameList = mutableListOf()
                    }
                    user.gameList?.add(newGameDificulty)

                    val intent = Intent(this, Game::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                } else {
                    showAlert("Error", "Debes seleccionar una dificultad.", this)
                }
            }
        }


    }
    private fun startViews() {
        rgDificulty = findViewById(R.id.rbEasyHard)
        rbEasy = findViewById(R.id.rbEasy)
        rbHard = findViewById(R.id.rbHard)
        bContinue = findViewById(R.id.bContinue)
    }
}

