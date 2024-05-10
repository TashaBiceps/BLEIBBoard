package com.example.bleibboard.ui.state

import com.example.bleibboard.domain.BtMPUData
import com.example.bleibboard.domain.TestScoresData

data class TestState (
    val status : Int = 0,
    val timeLeft : Int = 0,
    val trialsLeft : Int = 0,
    val testScore: TestScoresData = TestScoresData(0.0F, 0.0F, 0.0F),
    val originalCoordinates: List<BtMPUData> = emptyList(),
)