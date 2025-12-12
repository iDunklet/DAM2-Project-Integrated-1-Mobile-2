package com.example.CatPython
import java.io.Serializable

class User(
    // user basic stuff
    val name: String = "",
    val age: Int,

    // game data
    var gameList: MutableList<UserGameData>? = mutableListOf()
): Serializable
