package com.example.chatapp.models

data class User (
    val id:String = "",
    var username:String = "",
    var phone: String = "",
    var bio:String = "",
    var fullname:String = "",
    var state:String = "",
    var photoUrl:String = "empty"
)