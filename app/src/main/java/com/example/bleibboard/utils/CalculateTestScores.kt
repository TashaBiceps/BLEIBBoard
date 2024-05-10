package com.example.bleibboard.utils

import com.example.bleibboard.domain.BtMPUData
import kotlin.math.sqrt

class calculateTestScores {

    fun calculateAPSI(originalCoordinates: List<BtMPUData>) : Float {

        var APSI : Float = 0.0F
        var sumOfYdiffsqrd : Float = 0.0F
        var noOfSamples : Int = originalCoordinates.size

        originalCoordinates.forEach { pair ->
            sumOfYdiffsqrd += (0 - pair.yValue)*(0 - pair.yValue)
        }

        APSI = sqrt(sumOfYdiffsqrd / noOfSamples.toFloat())

        return APSI
    }

    fun calculateMLSI(originalCoordinates: List<BtMPUData>) : Float {

        var MLSI : Float = 0.0F
        var sumOfXdiffsqrd : Float = 0.0F
        var noOfSamples : Int = originalCoordinates.size

        originalCoordinates.forEach { pair ->
            sumOfXdiffsqrd += (0 - pair.xValue)*(0 - pair.xValue)
        }

        MLSI = sqrt(sumOfXdiffsqrd / noOfSamples.toFloat())

        return MLSI
    }

    fun calculateOSI(originalCoordinates: List<BtMPUData>) : Float {

        var OSI : Float = 0.0F
        var sumOfXdiffsqrd : Float = 0.0F
        var sumOfYdiffsqrd : Float = 0.0F
        var noOfSamples : Int = originalCoordinates.size

        originalCoordinates.forEach { pair ->
            sumOfYdiffsqrd += (0 - pair.yValue)*(0 - pair.yValue)
            sumOfXdiffsqrd += (0 - pair.xValue)*(0 - pair.xValue)
        }

        OSI = sqrt((sumOfYdiffsqrd + sumOfXdiffsqrd) / noOfSamples.toFloat())

        return OSI
    }
}