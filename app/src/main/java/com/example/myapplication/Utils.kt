package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime





fun setupExitButton(user: User,activity: Activity) {
    appendUserToJsonFile(activity, user)
    activity.finish()
}
fun saveJsonToInternal(activity: Activity, filename: String, json: String) {
    activity.openFileOutput(filename, Activity.MODE_PRIVATE).use {
        it.write(json.toByteArray())
    }
}

//Button try again(we save more data inside the User)
fun setupTryAgainButton(user: User,activity: Activity){
    //
    appendUserToJsonFile(activity, user)
    //call Game activity

    val intent = Intent(activity, GameDificulty::class.java)
    intent.putExtra("user_data_try_again", user)
    activity.startActivity(intent)


}
public fun showAlert(title: String, message: String, activity: AppCompatActivity) {
    val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
    builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .show()
}
fun appendUserToJsonFile(activity: Activity, user: User, filename: String = "export.json") {
    val gson = GsonBuilder()
        .setPrettyPrinting()  // Esto formatea el JSON
        .create()

    val existingJson = try {
        activity.openFileInput(filename).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        "[]"
    }

    val userArray: Array<User> = gson.fromJson(existingJson, Array<User>::class.java)
    val userList = userArray.toMutableList()

    userList.add(user)

    val newJson = gson.toJson(userList)
    saveJsonToInternal(activity, filename, newJson)
}





