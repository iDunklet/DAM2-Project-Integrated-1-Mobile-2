package com.example.myapplication
import java.io.Serializable

class User(
    // user basic stuff
    val name: String = "",
    val age: Int,

    // game data
    val gameList: MutableList<UserDataGame>? = mutableListOf()
): Serializable
