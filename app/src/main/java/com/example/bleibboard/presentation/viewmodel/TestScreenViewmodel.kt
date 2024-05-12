package com.example.bleibboard.presentation.viewmodel

import android.os.CountDownTimer
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.bleibboard.data.remote.ble.BLEDeviceConnection
import com.example.bleibboard.domain.BtMPUData
import com.example.bleibboard.domain.TestCoordinateState
import com.example.bleibboard.domain.TestScoresData
import com.example.bleibboard.presentation.state.TestState
import com.example.bleibboard.presentation.view.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@HiltViewModel
class TestScreenViewmodel @Inject constructor(
    private var activeConnection: MutableStateFlow<BLEDeviceConnection?>
): ViewModel() {

    private val activeDeviceCoordinates = activeConnection.flatMapLatest {
        it?.xandyvalues ?: flowOf(BtMPUData(0.0F, 0.0F))
    }

    private val _xOffsetState = MutableStateFlow(0f)
    private val _yOffsetState = MutableStateFlow(0f)

    private var collectionJob: Job? = null

    private val _originalCoordinates = MutableStateFlow(listOf(BtMPUData(0.0F, 0.0F)))

    private val _testStatus = MutableStateFlow(0)
    private val _timeLeft = MutableStateFlow(20)
    private val _trialsLeft = MutableStateFlow(3)
    private val _testScore = MutableStateFlow(TestScoresData(0.0F, 0.0F, 0.0F))

    private val _coordinateState = MutableStateFlow(TestCoordinateState())
    val coordinateState = combine(
        _coordinateState, _xOffsetState, _yOffsetState) { state, x, y ->
        state.copy (
            xOffsetState = _xOffsetState.value,
            yOffsetState = _yOffsetState.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TestCoordinateState())

    private val _testState = MutableStateFlow(TestState())
    val testState = combine(
        _testState, _testStatus, _timeLeft, _trialsLeft, _testScore) { state, status, timeLeft, trialsLeft, testScore ->
        state.copy (
            status = _testStatus.value,
            timeLeft = _timeLeft.value,
            trialsLeft = _trialsLeft.value,
            testScore = _testScore.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TestState())

    private val xComponents = MutableStateFlow(listOf<Float>())
    private val yComponents = MutableStateFlow(listOf<Float>())

    // TEST SCREEN FUNCTIONS

    //Countdown timer
    private val timer: CountDownTimer = object : CountDownTimer(20000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _timeLeft.value = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            _testStatus.value = 0
            //_trialsLeft.value -= 1
            _timeLeft.value = 20
            _testScore.value = calculateOSI_APSI_MLSI()
            _originalCoordinates.value = emptyList()
        }
    }

    //values for internal processing

    fun addPoint(x: Float, y: Float) {
        xComponents.value += x
        yComponents.value += y
    }

    suspend fun startTest() {
        val incomingMPUDataFlow: Flow<BtMPUData> = activeDeviceCoordinates

        collectionJob = viewModelScope.launch {
            incomingMPUDataFlow.collect { data ->
                _originalCoordinates.value += data
                _xOffsetState.value = data.xValue
                _yOffsetState.value = data.yValue
            }
        }

        viewModelScope.launch {
            timer.start()
        }

        _trialsLeft.value -= 1
        _testStatus.value += 1
    }

    fun stopTest() {
        collectionJob?.cancel()
        _testStatus.value = 2
        viewModelScope.launch {
            timer.cancel()
        }
    }

    fun resetTest() {
        collectionJob?.cancel()
        _xOffsetState.value = 0f
        _yOffsetState.value = 0f
        xComponents.value = emptyList<Float>()
        yComponents.value = emptyList<Float>()
    }

    fun restartTrial() {
        resetTest()
        _trialsLeft.value += 1
        _testStatus.value = 1
        _timeLeft.value = 20
        viewModelScope.launch {
            startTest()
        }
    }

    fun getPath() : Path {
        val path = Path()

        val xComponents = xComponents.value
        val yComponents = yComponents.value

        if (xComponents.size == yComponents.size) {
            for (i in xComponents.indices) {
                //_originalCoordinates.value += BtMPUData(xComponents[i], yComponents[i])
                if (i in 0..3 ) {
                    path.moveTo(xComponents[i], yComponents[i])
                } else {
                    path.lineTo(xComponents[i], yComponents[i])
                }
            }
        }

        return path

    }

    fun calculateOSI_APSI_MLSI(): TestScoresData {
        val originalCoordinates = _originalCoordinates.value
        val OSI : Float
        val APSI : Float
        val MLSI : Float

        var sumOfXdiffsqrd : Float = 0.0F
        var sumOfYdiffsqrd : Float = 0.0F
        var noOfSamples : Int = originalCoordinates.size

        originalCoordinates.forEach { pair ->
            sumOfYdiffsqrd += (0 - pair.yValue)*(0 - pair.yValue)
            sumOfXdiffsqrd += (0 - pair.xValue)*(0 - pair.xValue)
        }

        OSI = sqrt((sumOfYdiffsqrd + sumOfXdiffsqrd) / noOfSamples.toFloat())
        APSI = sqrt(sumOfYdiffsqrd / noOfSamples.toFloat())
        MLSI = sqrt(sumOfXdiffsqrd / noOfSamples.toFloat())

        return TestScoresData(OSI, APSI, MLSI)
    }

    fun saveTest() {

    }

    fun navToAthleteData(navController: NavController) {
        navController.navigate(Screens.Records.name)
    }


    fun mapRange(value: Float, fromRange1: Float, toRange1: Float, fromRange2: Float, toRange2: Float): Float {
        return (value - fromRange1) * (toRange2 - fromRange2) / (toRange1 - fromRange1) + fromRange2
    }
}