package com.example.train.model

import java.io.Serializable

data class Workout(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var duration: Int = 0,
    var exercises: List<Exercise> = emptyList()
) : Serializable