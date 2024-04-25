package com.example.bleibboard.ble

import com.example.bleibboard.domain.BtMPUData

fun String.toBtMPUData(isFromLocalUser: Boolean): BtMPUData {
    val xValue = substringBeforeLast("#").toFloat()
    val yValue = substringAfter("#").toFloat()

    return BtMPUData(
        xValue = xValue,
        yValue = yValue
    )
}