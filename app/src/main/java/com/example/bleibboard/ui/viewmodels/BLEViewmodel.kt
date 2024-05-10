package com.example.bleibboard.ui.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.ui.graphics.Path
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bleibboard.ble.BLEDeviceConnection
import com.example.bleibboard.ble.BLEScanner
import com.example.bleibboard.ble.PERMISSION_BLUETOOTH_CONNECT
import com.example.bleibboard.ble.PERMISSION_BLUETOOTH_SCAN
import com.example.bleibboard.domain.BtMPUData
import com.example.bleibboard.domain.TestScoresData
import com.example.bleibboard.ui.state.TestState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.jvm.internal.MagicApiIntrinsics
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalCoroutinesApi::class)
class BLEViewModel(private val application: Application): AndroidViewModel(application) {

    //Instantiate BLEScanner and states for BLE connection
    private val bleScanner = BLEScanner(application)
    private var activeConnection = MutableStateFlow<BLEDeviceConnection?>(null)

    private val isDeviceConnected = activeConnection.flatMapLatest {
        it?.isConnected ?: flowOf(false)
    }
    private val activeDeviceServices = activeConnection.flatMapLatest {
        it?.services ?: flowOf(emptyList())
    }

    private val activeDeviceCoordinates = activeConnection.flatMapLatest {
        it?.xandyvalues ?: flowOf(BtMPUData(0.0F, 0.0F))
    }

    private val _xOffsetState = MutableStateFlow(0f)
    private val _yOffsetState = MutableStateFlow(0f)

    private val _uiState = MutableStateFlow(BLEUIState())
    val uiState = combine(
        _uiState,
        isDeviceConnected,
        activeDeviceServices,
        _xOffsetState,
        _yOffsetState
    ) { state : BLEUIState,
        isDeviceConnected : Boolean,
        services : List<BluetoothGattService>,
        xcoordinate : Float,
        ycoordinate : Float ->
        state.copy(
            isDeviceConnected = isDeviceConnected,
            discoveredCharacteristics = services.associate {
                    service -> Pair(service.uuid.toString(), service.characteristics.map { it.uuid.toString() }) },
            xOffsetState = xcoordinate,
            yOffsetState = ycoordinate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BLEUIState())

    private var collectionJob: Job? = null

    //Instantiate states for test screen
    private val _originalCoordinates = MutableStateFlow(listOf(BtMPUData(0.0F, 0.0F)))

    private val _testStatus = MutableStateFlow(0)
    private val _timeLeft = MutableStateFlow(20)
    private val _trialsLeft = MutableStateFlow(3)
    private val _testScore = MutableStateFlow(TestScoresData(0.0F, 0.0F, 0.0F))

    private val _testUiState = MutableStateFlow(TestState())
    val testUiState = combine(
        _testUiState, _testStatus, _timeLeft, _trialsLeft, _testScore) { state, status, timeLeft, trialsLeft, testScore ->
        state.copy (
            status = _testStatus.value,
            timeLeft = _timeLeft.value,
            trialsLeft = _trialsLeft.value,
            testScore = _testScore.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TestState())

    private val xComponents = MutableStateFlow(listOf<Float>())
    private val yComponents = MutableStateFlow(listOf<Float>())

    init {
        viewModelScope.launch {
            bleScanner.foundDevices.collect { devices ->
                _uiState.update { it.copy(foundDevices = devices) }
            }
        }
        viewModelScope.launch {
            bleScanner.isScanning.collect { isScanning ->
                _uiState.update { it.copy(isScanning = isScanning) }
            }
        }
    }

    @RequiresPermission(allOf = [PERMISSION_BLUETOOTH_SCAN, PERMISSION_BLUETOOTH_CONNECT])
    fun startScanning() {
        bleScanner.startScanning()
        /*
        viewModelScope.launch {
            bleScanner.foundDevices.collect { deviceList ->
                deviceList.find { it.name == "ESP32" }?.let { device ->
                    Log.e("BLEViewModel", "Found ESP32 device")
                    stopScanning()
                    setActiveDevice(device)
                    connectActiveDevice()
                }
            }
        }

         */
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun stopScanning() {
        bleScanner.stopScanning()
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(allOf = [PERMISSION_BLUETOOTH_CONNECT, PERMISSION_BLUETOOTH_SCAN])
    fun setActiveDevice(device: BluetoothDevice?) {
        activeConnection.value = device?.run { BLEDeviceConnection(application, device) }
        _uiState.update { it.copy(activeDevice = device) }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connectActiveDevice() {
        activeConnection.value?.connect()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnectActiveDevice() {
        activeConnection.value?.disconnect()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverActiveDeviceServices() {
        activeConnection.value?.discoverServices()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun beginNotificationsFromActiveDevice() {
        activeConnection.value?.startReceiving()
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()

        //when the ViewModel dies, shut down the BLE client with it
        if (bleScanner.isScanning.value) {
            if (ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bleScanner.stopScanning()
            }
        }
    }

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


    fun mapRange(value: Float, fromRange1: Float, toRange1: Float, fromRange2: Float, toRange2: Float): Float {
        return (value - fromRange1) * (toRange2 - fromRange2) / (toRange1 - fromRange1) + fromRange2
    }
}

data class BLEUIState(
    val isScanning: Boolean = false,
    val foundDevices: List<BluetoothDevice> = emptyList(),
    val activeDevice: BluetoothDevice? = null,
    val isDeviceConnected: Boolean = false,
    val discoveredCharacteristics: Map<String, List<String>> = emptyMap(),
    val xOffsetState : Float = 0.0F,
    val yOffsetState : Float = 0.0F,
    val pathPoints : List<Pair<Float, Float>> = emptyList()
)