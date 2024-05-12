package com.example.bleibboard.presentation.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.bleibboard.data.remote.ble.BLEDeviceConnection
import com.example.bleibboard.data.remote.ble.PERMISSION_BLUETOOTH_CONNECT
import com.example.bleibboard.data.remote.ble.PERMISSION_BLUETOOTH_SCAN
import com.example.bleibboard.presentation.state.ActiveDeviceState
import com.example.bleibboard.presentation.view.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ActiveDeviceViewmodel @Inject constructor(
    private var activeConnection: MutableStateFlow<BLEDeviceConnection?>
): ViewModel() {

    private val isDeviceConnected = activeConnection.flatMapLatest {
        it?.isConnected ?: flowOf(false)
    }
    private val activeDeviceServices = activeConnection.flatMapLatest {
        it?.services ?: flowOf(emptyList())
    }

    private val _activeDeviceState = MutableStateFlow(ActiveDeviceState())
    val activeDeviceState = combine(
        _activeDeviceState,
        isDeviceConnected,
        activeDeviceServices
    ) { state, isConnected, services ->
        state.copy(
            isDeviceConnected = isConnected,
            activeDeviceServices = services.associate {
                    service -> Pair(service.uuid.toString(), service.characteristics.map { it.uuid.toString() }) }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActiveDeviceState())

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

    fun navigateBack(navController : NavController) {
        navController.popBackStack()
    }

    fun navigateToTest(navController : NavController) {
        navController.navigate(Screens.Test.name)
    }

}