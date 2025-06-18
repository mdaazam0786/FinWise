package com.example.finwise.data

data class User (
    val userId : String? = "",
    val firstName: String = "",
    val lastName: String = "",
    var email: String = "",
    var password : String = "",
    val imageUrl : String? = ""
)