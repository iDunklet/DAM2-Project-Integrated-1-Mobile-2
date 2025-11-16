package com.example.myapplication

import android.widget.ImageView
import android.widget.TextView
import android.os.Handler

data class Gota(
    val imagen: ImageView,
    val numeros: List<Game.Numeros>,
    val texto: TextView,
    var handler: Handler? = null,
    var runnable: Runnable? = null,
    var creationTime: Long = 0L

)