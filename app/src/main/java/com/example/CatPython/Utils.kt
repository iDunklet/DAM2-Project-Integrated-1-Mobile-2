package com.example.CatPython

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

fun setupExitButton(user: User, activity: Activity) {
    appendUserToJsonFile(activity, user) // save progress
    activity.finishAffinity()            // close all Activities
    android.os.Process.killProcess(android.os.Process.myPid()) // kill the process
}

fun saveJsonToInternal(activity: Activity, filename: String, json: String) {
    val file = File(activity.filesDir, filename)
    file.writeText(json) // creates the file if it does not exist, overwrites if it does
}

// Button "try again" (we save more data inside the User)
fun setupTryAgainButton(user: User, activity: Activity) {
    appendUserToJsonFile(activity, user)

    val intent = Intent(activity, GameDificulty::class.java)
    intent.putExtra("user_data_try_again", user)
    activity.startActivity(intent)
}

fun showAlert(title: String, message: String, activity: AppCompatActivity) {
    val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
    builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .show()
}

fun appendUserToJsonFile(activity: Activity, user: User, filename: String = "export.json") {
    val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    // 1. Read existing JSON or create an empty one
    val existingJson = readJsonFile(activity, filename)

    // 2. Convert JSON to map
    val existingMap = parseJsonToMap(gson, existingJson)

    // 3. Update user list
    val updatedList = updateUserList(existingMap, user)

    // 4. Convert to JSON and save
    val newJson = gson.toJson(mapOf("cat" to updatedList))
    saveJsonToInternal(activity, filename, newJson)
}

private fun readJsonFile(activity: Activity, filename: String): String {
    val file = File(activity.filesDir, filename)
    return if (file.exists()) {
        file.readText()
    } else {
        """{"cat":[]}""" // initial JSON if the file does not exist
    }
}

private fun parseJsonToMap(gson: Gson, json: String): Map<String, List<User>> {
    val type = object : TypeToken<Map<String, List<User>>>() {}.type
    return try {
        gson.fromJson(json, type) ?: mapOf("cat" to emptyList())
    } catch (e: Exception) {
        mapOf("cat" to emptyList()) // if JSON is corrupted, start empty
    }
}

private fun updateUserList(existingMap: Map<String, List<User>>, user: User): MutableList<User> {
    val userList = existingMap["cat"]?.toMutableList() ?: mutableListOf()
    userList.add(user)
    return userList
}