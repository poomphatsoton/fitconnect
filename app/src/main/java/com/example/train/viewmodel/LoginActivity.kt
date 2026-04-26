package com.example.train.viewmodel

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.train.database.DatabaseHelper
import com.example.train.ui.CreateAccountActivity
import com.example.train.ui.MainActivity
import com.example.train.view.LoginScreen
import androidx.core.content.edit

class Login2Activity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = DatabaseHelper(this)

        val prefs = getSharedPreferences("FitConnect", MODE_PRIVATE)

        if (prefs.contains("userId")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            LoginScreen(
                onLoginClick = { username, password ->
                    login(username, password)
                },
                onCreateAccountClick = {
                    startActivity(Intent(this, CreateAccountActivity::class.java))
                }
            )
        }
    }

    private fun login(username: String, password: String) {
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
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showInvalidLogin()
            }
        }
    }

    private fun showInvalidLogin() {
        Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
    }

    private fun saveUserSession(userId: Int, role: String) {
        getSharedPreferences("FitConnect", MODE_PRIVATE).edit {
            putInt("userId", userId)
            putString("role", role)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}


