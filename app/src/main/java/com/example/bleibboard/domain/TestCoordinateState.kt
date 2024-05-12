package com.example.bleibboard.domain

data class TestCoordinateState (
    val xOffsetState : Float = 0.0F,
    val yOffsetState : Float = 0.0F,
    val pathPoints : List<Pair<Float, Float>> = emptyList(),
)