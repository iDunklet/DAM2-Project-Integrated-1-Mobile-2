package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import java.time.LocalDateTime

//tests




//Button exit(we export here)
//cosa del dia
/*@RequiresApi(Build.VERSION_CODES.O)
fun setupExitButton(activity: Activity, button: ImageButton) {
    //export
    val g = Gson()

    //necesito objeto natalia
    val export = g.toJson(user)
    println(export)
    //exit
    //activity.finish()
}*/
//Button try again(we save more data inside the User)
fun setupTryAgainButton(activity: Activity, button: ImageButton){
    //


    //call Game activity
    val intent = Intent(activity, Game::class.java)
    activity.startActivity(intent)


}
public fun showAlert(title: String, message: String, activity: AppCompatActivity) {
    val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
    builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .show()
}




