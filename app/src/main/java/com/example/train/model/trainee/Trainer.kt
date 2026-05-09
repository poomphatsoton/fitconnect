package com.example.train.model.trainee

import com.example.train.model.Tag

data class TraineeTrainerUiState(
    val myTrainer: TrainerProfile? = null,
    val otherTrainers: List<TrainerProfile> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val searchQuery: String = "",
    val selectedTags: List<Tag> = emptyList()
)

data class TrainerProfile(
    val id: Int,
    val name: String,
    val bio: String,
    val tags: List<Tag> = emptyList(),
    val activeTrainees: Int = 0,
    val maxTrainees: Int = 0
) {
    val availableTrainees: Int
        get() = (maxTrainees - activeTrainees).coerceAtLeast(0)
}
