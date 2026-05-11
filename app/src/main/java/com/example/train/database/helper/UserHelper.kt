package com.example.train.database.helper

import android.content.ContentValues
import android.database.Cursor
import com.example.train.database.DatabaseHelper
import com.example.train.security.PasswordHasher

class UserHelper(private val dbHelper: DatabaseHelper) {

    fun getUserByUsername(username: String): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(
                DatabaseHelper.COL_USER_ID,
                DatabaseHelper.COL_USER_ROLE,
                DatabaseHelper.COL_USER_PASSWORD
            ),
            "${DatabaseHelper.COL_USER_USERNAME} = ?",
            arrayOf(username),
            null,
            null,
            null
        )
    }

    fun getUserById(userId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
    }

    fun getTraineeUserById(traineeId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_USER_ID} = ? AND ${DatabaseHelper.COL_USER_ROLE} = ?",
            arrayOf(traineeId.toString(), "trainee"),
            null,
            null,
            null
        )
    }

    fun insertUser(username: String, password: String, role: String, name: String, bio: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USER_USERNAME, username)
            put(DatabaseHelper.COL_USER_PASSWORD, PasswordHasher.hash(password))
            put(DatabaseHelper.COL_USER_ROLE, role)
            put(DatabaseHelper.COL_USER_NAME, name)
            put(DatabaseHelper.COL_USER_BIO, bio)
        }
        return db.insert(DatabaseHelper.TABLE_USERS, null, values)
    }

    fun updateUserProfile(userId: Int, name: String, bio: String, maxTrainees: Int? = null, password: String?) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_USER_NAME, name)
            put(DatabaseHelper.COL_USER_BIO, bio)
            if (maxTrainees != null) {
                put(DatabaseHelper.COL_USER_MAX_TRAINEES, maxTrainees)
            }
            if (password != null) {
                put(DatabaseHelper.COL_USER_PASSWORD, PasswordHasher.hash(password))
            }
        }
        db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }
}
