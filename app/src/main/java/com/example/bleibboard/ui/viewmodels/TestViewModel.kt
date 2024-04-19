package com.example.bleibboard.ui.viewmodels

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.example.BLEIBBoard.domain.bluetooth.BtMPUData
import kotlinx.coroutines.flow.MutableStateFlow

class TestViewModel : ViewModel() {

    val testState : Boolean = false
    val xComponents: List<Float> = emptyList(),
    val yComponents: List<Float> = emptyList(),
    val xOffsetState: Float = 0f,
    val yOffsetState: Float = 0f

    private val _uiState = MutableStateFlow(TestUIState())
    private val uiState = combine(
        _uiState
        testState,
        xComponents,
        yComponents,
    ) { state, isDeviceConnected, services ->
        state.copy(
            isDeviceConnected = isDeviceConnected,
            discoveredCharacteristics = services.associate { service -> Pair(service.uuid.toString(), service.characteristics.map { it.uuid.toString() }) },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TestUIState())
    )

    private val xComponents = savedStateHandle.getStateFlow("xComponents", emptyList<Float>())
    private val yComponents = savedStateHandle.getStateFlow("yComponents", emptyList<Float>())

    val xOffsetState = savedStateHandle.getStateFlow("xOffsetState", 0f)
    val yOffsetState = savedStateHandle.getStateFlow("yOffsetState", 0f)

    fun addPoint(x: Float, y: Float) {
        val xComponents = xComponents.value + x
        val yComponents = yComponents.value + y

        savedStateHandle["xComponents"] = xComponents
        savedStateHandle["yComponents"] = yComponents

        savedStateHandle["xOffsetState"] = x
        savedStateHandle["yOffsetState"] = y
    }
}

data class TestUIState(
    val testState : Boolean = false
    val xComponents: List<Float> = emptyList(),
    val yComponents: List<Float> = emptyList(),
    val xOffsetState: Float = 0f,
    val yOffsetState: Float = 0f
)
