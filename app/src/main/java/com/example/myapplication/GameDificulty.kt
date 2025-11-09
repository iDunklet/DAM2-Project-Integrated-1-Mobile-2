package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDateTime


class GameDificulty : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var rgDificulty: RadioGroup
    private lateinit var rbEasy: RadioButton
    private lateinit var rbHard: RadioButton
    private lateinit var bContinue: Button

    private lateinit var difficulty: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_difculty)
        startViews()
        user = intent.getSerializableExtra("user_data") as User
        rgDificulty.setOnCheckedChangeListener (object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
                if (checkedId == R.id.rbEasy){
                    difficulty = "easy"
                }else{
                    difficulty = "hard"
                }
            }
        })
        bContinue.setOnClickListener {
            if (difficulty != null) {
                val newGameDificulty = UserDataGame(
                    gameTime = null,
                    errors = null,
                    points = null,
                    dificulty = difficulty
                )
                user.gameList?.add(newGameDificulty)


                val intent = Intent(this, Game::class.java)
                intent.putExtra("user_data", user)
                startActivity(intent)

            } else {
                showAlert("Error", "Debes seleccionar una dificultad.",this)
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

