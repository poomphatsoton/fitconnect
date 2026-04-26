package com.example.train.model

data class TraineeRequest(
    var id: Int = 0,
    var trainerId: Int = 0,
    var traineeId: Int = 0,
    var status: String? = null,
    var trainee: User? = null
)