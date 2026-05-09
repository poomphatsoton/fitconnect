package com.example.train.viewmodel.authentication

import android.app.Application
import android.database.Cursor
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper

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

        val projection = arrayOf(
            DatabaseHelper.COL_USER_ID,
            DatabaseHelper.COL_USER_ROLE,
            DatabaseHelper.COL_USER_PASSWORD
        )

        val selection = "${DatabaseHelper.COL_USER_USERNAME} = ?"
        val selectionArgs = arrayOf(trimmedUsername)

        val cursor: Cursor? = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_USERS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val storedPassword = it.getString(
                    it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD)
                )

                if (storedPassword != trimmedPassword) {
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
