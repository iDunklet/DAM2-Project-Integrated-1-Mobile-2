package com.example.myapplication

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserGameData(
    var gameTime: Int? = null,
    var errors: Int? = null,
    var points: Int? = null,
    var date: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),
    val dificulty: String,
    var reactionTime: MutableList<Float>
                  ) : Serializable