package com.example.bleibboard.presentation.state

import android.bluetooth.BluetoothDevice

data class ScannerState (
    val isScanning: Boolean = false,
    val foundDevices: List<BluetoothDevice> = emptyList(),
    val activeDevice: BluetoothDevice? = null
)