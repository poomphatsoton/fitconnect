package com.example.train.viewmodel.trainer

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.Trainee
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

        val activeCount = dbHelper.getActiveTraineesCount(userId)
        val pendingCount = dbHelper.getPendingRequestsCount(userId)
        
        val trainees = mutableListOf<Trainee>()
        val cursor = dbHelper.getAllTrainees(userId)
        
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)) ?: "Unknown"
                val bio = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO)) ?: ""
                
                trainees.add(
                    Trainee(
                        id = id,
                        name = name,
                        bio = bio,
                        tags = emptyList() // Tags implementation can be added later if needed
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        uiState.value = TraineesUiState(
            activeCount = activeCount,
            requestCount = pendingCount,
            allActiveTrainees = trainees
        )
    }
}