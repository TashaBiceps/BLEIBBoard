package com.example.bleibboard.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TestsDao {

    @Upsert
    suspend fun upsertTest(test: Tests)

    @Delete
    suspend fun deleteTest(test: Tests)

    @Query("SELECT * FROM tests ORDER BY firstName ASC")
    fun queryTestsOrderedByFirstName(): Flow<List<Tests>>

    @Query("SELECT * FROM tests ORDER BY lastName ASC")
    fun queryTestsOrderedByLastName(): Flow<List<Tests>>

    @Query("SELECT * FROM tests ORDER BY date ASC")
    fun queryTestsOrderedByDate(): Flow<List<Tests>>
}