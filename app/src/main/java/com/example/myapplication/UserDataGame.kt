package com.example.myapplication

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserDataGame(
    val gameTime: Int? = null,
    val errors: Int? = null,
    val points: Int? = null,
    val date: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),
    val dificulty: String
) : Serializable
