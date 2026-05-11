package com.example.train.database.helper

import android.content.ContentValues
import android.database.Cursor
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag

class TagHelper(private val dbHelper: DatabaseHelper) {

    fun getUserTags(userId: Int): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT s.${DatabaseHelper.COL_USER_ID}, w.${DatabaseHelper.COL_TAG_ID}, w.${DatabaseHelper.COL_TAG_NAME}
            FROM ${DatabaseHelper.TABLE_USERS_TAGS} s
            LEFT JOIN ${DatabaseHelper.TABLE_TAGS} w
                ON s.${DatabaseHelper.COL_TAG_ID} = w.${DatabaseHelper.COL_TAG_ID}
            WHERE s.${DatabaseHelper.COL_USER_ID} = ?
        """.trimIndent()

        return db.rawQuery(query, arrayOf(userId.toString()))
    }

    fun getAllTags(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_TAGS,
            null,
            null,
            null,
            null,
            null,
            DatabaseHelper.COL_TAG_NAME
        )
    }

    fun updateUserTags(userId: Int, tags: List<Tag>) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_USERS_TAGS, "${DatabaseHelper.COL_USER_ID} = ?", arrayOf(userId.toString()))

        tags.forEach { tag ->
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_USER_ID, userId)
                put(DatabaseHelper.COL_TAG_ID, tag.tagId)
            }
            db.insert(DatabaseHelper.TABLE_USERS_TAGS, null, values)
        }
    }

    fun getExerciseTags(exerciseId: Long): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT w.${DatabaseHelper.COL_TAG_ID}, w.${DatabaseHelper.COL_TAG_NAME}
            FROM ${DatabaseHelper.TABLE_EXERCISE_TAGS} s
            LEFT JOIN ${DatabaseHelper.TABLE_TAGS} w
                ON s.${DatabaseHelper.COL_TAG_ID} = w.${DatabaseHelper.COL_TAG_ID}
            WHERE s.${DatabaseHelper.COL_EXERCISE_ID} = ?
        """.trimIndent()

        return db.rawQuery(query, arrayOf(exerciseId.toString()))
    }
}
