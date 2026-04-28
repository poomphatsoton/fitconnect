package com.example.train.viewmodel.trainer
import android.database.Cursor

fun <T> Cursor.mapToList(mapper: Cursor.() -> T): List<T> {
    val list = mutableListOf<T>()

    use { cursor ->
        while (cursor.moveToNext()) {
            list.add(cursor.mapper())
        }
    }

    return list
}