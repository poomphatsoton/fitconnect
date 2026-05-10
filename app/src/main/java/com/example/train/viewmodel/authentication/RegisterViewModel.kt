package com.example.train.viewmodel.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper

class RegistrationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    fun register(
        username: String,
        password: String,
        confirmPassword: String,
        name: String,
        bio: String,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()
        val trimmedConfirmPassword = confirmPassword.trim()
        val trimmedName = name.trim()
        val trimmedBio = bio.trim()

        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            onError("Username and password cannot be empty")
            return
        }

        if (trimmedPassword != trimmedConfirmPassword) {
            onError("Passwords do not match")
            return
        }

        try {
            val result = dbHelper.insertUser(
                username = trimmedUsername,
                password = trimmedPassword,
                role = role,
                name = trimmedName,
                bio = trimmedBio
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
