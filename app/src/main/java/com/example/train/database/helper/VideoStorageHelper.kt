package com.example.train.database.helper

import android.content.ContentValues
import android.net.Uri
import com.example.train.database.DatabaseHelper
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await

class VideoStorageHelper(
    private val dbHelper: DatabaseHelper,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    suspend fun uploadExerciseVideo(exerciseId: Int, uri: Uri, videoName: String): String {
        val url = uploadToStorage(exerciseId, uri)
        saveExerciseVideo(exerciseId, url, videoName)
        return url
    }

    suspend fun uploadToStorage(exerciseId: Int, uri: Uri): String {
        val ref = storage.reference.child(
            "exercise_videos/$exerciseId/${System.currentTimeMillis()}.mp4"
        )
        val metadata = StorageMetadata.Builder()
            .setContentType("video/mp4")
            .build()

        ref.putFile(uri, metadata).await()
        return ref.downloadUrl.await().toString()
    }

    fun saveExerciseVideo(exerciseId: Int, url: String, videoName: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_EXERCISE_VIDEO_URL, url)
            put(DatabaseHelper.COL_EXERCISE_VIDEO_NAME, videoName)
        }

        db.update(
            DatabaseHelper.TABLE_EXERCISES,
            values,
            "${DatabaseHelper.COL_EXERCISE_ID} = ?",
            arrayOf(exerciseId.toString())
        )
    }
}
