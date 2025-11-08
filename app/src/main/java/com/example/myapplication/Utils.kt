package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import java.time.LocalDateTime

//tests
@RequiresApi(Build.VERSION_CODES.O)
val user = User(
    name = "Test",
    age = 20,
    listaDeClases = mutableListOf(
        UserDataGame(
            gameTime = 120,
            errors = 3,
            points = 4500,
            date = LocalDateTime.now(),
            dificulty = "hard"
        ),
        UserDataGame(
            gameTime = 85,
            errors = 1,
            points = 5200,
            date = LocalDateTime.now().minusDays(1),
            //api ns que 26
            dificulty = "easy"
        )
    )
)


//Button exit(we export here)
//cosa del dia
@RequiresApi(Build.VERSION_CODES.O)
fun setupExitButton(activity: Activity, button: ImageButton) {
    //export
    val g = Gson()

    //necesito objeto natalia
    val export = g.toJson(user)
    println(export)
    //exit
    //activity.finish()
}
//Button try again(we save more data inside the User)
fun setupTryAgainButton(activity: Activity, button: ImageButton){
    //


    //call Game activity
    val intent = Intent(activity, Game::class.java)
    activity.startActivity(intent)


}




