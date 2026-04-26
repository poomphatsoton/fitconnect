package com.example.train.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.train.database.DatabaseHelper
import com.example.train.databinding.ActivityCreateAccountBinding

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DatabaseHelper(this)

        binding.btnRegister.setOnClickListener { register() }
    }

    private fun register() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val name = binding.etName.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()

        val role = if (binding.rgRole.checkedRadioButtonId == binding.rbTrainer.id) {
            "trainer"
        } else {
            "trainee"
        }

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val values = android.content.ContentValues().apply {
            put(DatabaseHelper.COL_USER_USERNAME, username)
            put(DatabaseHelper.COL_USER_PASSWORD, password)
            put(DatabaseHelper.COL_USER_ROLE, role)
            put(DatabaseHelper.COL_USER_NAME, name)
            put(DatabaseHelper.COL_USER_BIO, bio)
        }

        try {
            val result = dbHelper.writableDatabase.insert(DatabaseHelper.TABLE_USERS, null, values)
            if (result != -1L) {
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
        }
    }
}