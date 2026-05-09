package com.example.train.viewmodel.authentication

import android.app.Application
import android.content.ContentValues
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.security.PasswordHasher

class RegistrationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    fun register(
        username: String,
        password: String,
        name: String,
        bio: String,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()
        val trimmedName = name.trim()
        val trimmedBio = bio.trim()

        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            onError("Username and password cannot be empty")
            return
        }

        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USER_USERNAME, trimmedUsername)
            put(DatabaseHelper.COL_USER_PASSWORD, PasswordHasher.hash(trimmedPassword))
            put(DatabaseHelper.COL_USER_ROLE, role)
            put(DatabaseHelper.COL_USER_NAME, trimmedName)
            put(DatabaseHelper.COL_USER_BIO, trimmedBio)
        }

        try {
            val result = dbHelper.writableDatabase.insert(
                DatabaseHelper.TABLE_USERS,
                null,
                values
            )

            if (result != -1L) {
                onSuccess()
            } else {
                onError("Registration failed")
            }

        } catch (e: Exception) {
            onError("Username already exists")
        }
    }
}
