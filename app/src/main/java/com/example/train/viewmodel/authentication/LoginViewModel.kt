package com.example.train.viewmodel.authentication

import android.app.Application
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.security.PasswordHasher

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val dbHelper = DatabaseHelper(context)
    private val prefs = context.getSharedPreferences("FitConnect", 0)

    fun isLoggedIn(): Boolean {
        return prefs.contains("userId")
    }

    fun getUserRole(): String? {
        return prefs.getString("role", null)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun login(
        username: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        if (trimmedUsername.isEmpty() || trimmedPassword.isEmpty()) {
            showInvalidLogin()
            return
        }

        dbHelper.getUserByUsername(trimmedUsername).use {
            if (it.moveToFirst()) {
                val storedPassword = it.getString(
                    it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD)
                )

                if (!PasswordHasher.verify(trimmedPassword, storedPassword)) {
                    showInvalidLogin()
                    return
                }

                val userId = it.getInt(
                    it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)
                )

                val role = it.getString(
                    it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ROLE)
                )

                saveUserSession(userId, role)
                onSuccess()
            } else {
                showInvalidLogin()
            }
        }
    }

    private fun showInvalidLogin() {
        Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
    }

    private fun saveUserSession(userId: Int, role: String) {
        context.getSharedPreferences("FitConnect", 0).edit {
            putInt("userId", userId)
            putString("role", role)
        }
    }
}
