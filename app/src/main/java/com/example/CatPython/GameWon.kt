package com.example.CatPython

import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class GameWon : AppCompatActivity() {

    private lateinit var bExit: ImageButton
    private lateinit var bTryAgain: ImageButton
    private lateinit var user: User

    private lateinit var swingAnimation: android.view.animation.Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_won)
        startViews()
        //user fake
        user = intent.getSerializableExtra("user_data") as User



        bExit.setOnClickListener {
            bExit.startAnimation(swingAnimation)
            Handler().postDelayed({
                setupExitButton(user, this)
            }, 600)
        }

        bTryAgain.setOnClickListener {
            bTryAgain.startAnimation(swingAnimation)
            Handler().postDelayed({
                setupTryAgainButton(user, this)
            }, 600)
        }

    }

    private fun startViews() {
        bExit = findViewById<ImageButton>(R.id.button_exit)
        bTryAgain = findViewById<ImageButton>(R.id.button_try_again)
        swingAnimation = AnimationUtils.loadAnimation(this, R.anim.button_exit_tryagain_swing)

    }
}