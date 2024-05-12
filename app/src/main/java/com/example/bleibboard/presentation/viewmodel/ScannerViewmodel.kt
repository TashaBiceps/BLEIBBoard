package com.example.bleibboard.presentation.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.bleibboard.data.remote.ble.BLEDeviceConnection
import com.example.bleibboard.data.remote.ble.BLEScanner
import com.example.bleibboard.data.remote.ble.PERMISSION_BLUETOOTH_CONNECT
import com.example.bleibboard.data.remote.ble.PERMISSION_BLUETOOTH_SCAN
import com.example.bleibboard.presentation.state.ScannerState
import com.example.bleibboard.presentation.view.Screens
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewmodel @Inject constructor(
    private val bleScanner: BLEScanner,
    private val activeConnection: MutableStateFlow<BLEDeviceConnection?>
): AndroidViewModel(Application()) {

    val _scannerState = MutableStateFlow(ScannerState())
    val scannerState = _scannerState.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ScannerState())

    init {
        viewModelScope.launch {
            bleScanner.foundDevices.collect { devices ->
                _scannerState.update { it.copy(foundDevices = devices) }
            }
        }
        viewModelScope.launch {
            bleScanner.isScanning.collect { isScanning ->
                _scannerState.update { it.copy(isScanning = isScanning) }
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
        activeConnection.value = device?.run { BLEDeviceConnection(getApplication(), device) }
        _scannerState.update { it.copy(activeDevice = device) }
    }

    fun navigateToDevice(navController : NavController) {
        navController.navigate(Screens.Device.name)
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
}