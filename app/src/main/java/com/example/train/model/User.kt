package com.example.train.model

data class User(
    var id: Int = 0,
    var username: String? = null,
    var password: String? = null,
    var role: String? = null,
    var name: String? = null,
    var bio: String? = null
)