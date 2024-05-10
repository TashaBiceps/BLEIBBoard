package com.example.bleibboard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Tests::class],
    version = 1
)
abstract class TestDatabase: RoomDatabase() {

    abstract val dao: TestsDao

}