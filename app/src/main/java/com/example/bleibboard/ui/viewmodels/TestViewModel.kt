package com.example.bleibboard.ui.viewmodels

import android.bluetooth.BluetoothDevice
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.example.bleibboard.domain.BtMPUData
import kotlinx.coroutines.flow.MutableStateFlow

class TestViewModel : ViewModel() {

    val testState = MutableStateFlow(false)
    val xComponents = MutableStateFlow(listOf<Float>())
    val yComponents = MutableStateFlow(listOf<Float>())
    val xOffsetState = MutableStateFlow(0f)
    val yOffsetState = MutableStateFlow(0f)

    fun addPoint(x: Float, y: Float) {
        xComponents.value += x
        yComponents.value += y
        xOffsetState.value = x
        yOffsetState.value = y
    }

    fun clearPoints() {
        xComponents.value = emptyList<Float>()
        yComponents.value = emptyList<Float>()
    }

    fun getPath() : Path {
        val path = Path()
        val xComponents = xComponents.value
        val yComponents = yComponents.value

        if (xComponents.size == yComponents.size) {
            for (i in xComponents.indices) {
                if (i == 0) {
                    path.moveTo(xComponents[i], yComponents[i])
                } else {
                    path.lineTo(xComponents[i], yComponents[i])
                }
            }
        }
        return path
    }
}