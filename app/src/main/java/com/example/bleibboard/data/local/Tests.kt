package com.example.bleibboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tests(
    val date: String,
    val time: String,
    val firstName: String,
    val lastName: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
