package com.example.train.viewmodel.trainer

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.TraineesUiState

class TraineesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = mutableStateOf(TraineesUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    fun loadTabData() {
        if (userId == -1) return

        val active = dbHelper.getActiveTraineesCount(userId)
        val pending = dbHelper.getPendingRequestsCount(userId)

        uiState.value = TraineesUiState(
            activeCount = active,
            requestCount = pending
        )
    }
}