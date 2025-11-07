package com.example.myapplication

import java.time.LocalDateTime

class User(
    // user basic stuff
    val name: String = "",
    val age: Int,

    // game data
    val listaDeClases: MutableList<UserDataGame> = mutableListOf()
)
