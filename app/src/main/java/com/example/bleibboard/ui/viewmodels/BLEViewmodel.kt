package com.example.bleibboard.ui.viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
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

@OptIn(ExperimentalCoroutinesApi::class)
class BLEViewModel(private val application: Application): AndroidViewModel(application) {
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

    private var collectionJob: Job? = null


    val xComponents = MutableStateFlow(listOf<Float>())
    val yComponents = MutableStateFlow(listOf<Float>())

    val xOffsetState = MutableStateFlow(0f)
    val yOffsetState = MutableStateFlow(0f)


    private val _uiState = MutableStateFlow(BLEUIState())
    val uiState = combine(
        _uiState,
        isDeviceConnected,
        activeDeviceServices,
        xOffsetState,
        yOffsetState
    ) { state, isDeviceConnected, services, xcoordinate, ycoordinate ->
        state.copy(
            isDeviceConnected = isDeviceConnected,
            discoveredCharacteristics = services.associate {
                service -> Pair(service.uuid.toString(), service.characteristics.map { it.uuid.toString() }) },
            xOffsetState = xcoordinate,
            yOffsetState = ycoordinate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BLEUIState())

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

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun startScanning() {
        bleScanner.startScanning()
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

    //values for internal processing

    fun addPoint(x: Float, y: Float) {
        xComponents.value += x
        yComponents.value += y
        xOffsetState.value = x
        yOffsetState.value = y
    }

    suspend fun startTest() {
        val incomingMPUDataFlow: Flow<BtMPUData> = activeDeviceCoordinates


        collectionJob = viewModelScope.launch {
            incomingMPUDataFlow.collect { data ->
                addPoint(data.xValue, data.yValue)
            }
        }
        /*
        incomingMPUDataFlow.collect { data ->
            addPoint(data.xValue, data.yValue)

        }
        */
    }

    fun stopTest() {
        collectionJob?.cancel()
    }

    fun clearTest() {
        collectionJob?.cancel()
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

data class BLEUIState(
    val isScanning: Boolean = false,
    val foundDevices: List<BluetoothDevice> = emptyList(),
    val activeDevice: BluetoothDevice? = null,
    val isDeviceConnected: Boolean = false,
    val discoveredCharacteristics: Map<String, List<String>> = emptyMap(),
    val xOffsetState : Float = 0.0F,
    val yOffsetState : Float = 0.0F
)