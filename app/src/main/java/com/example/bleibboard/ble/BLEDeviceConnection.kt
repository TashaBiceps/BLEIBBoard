package com.example.bleibboard.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.bleibboard.domain.BtMPUData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

val CTF_SERVICE_UUID: UUID = UUID.fromString("ccc82ba1-72c8-49ba-b712-2e49bad52eef")
val PASSWORD_CHARACTERISTIC_UUID: UUID = UUID.fromString("27501e93-1513-4074-866e-6bc3580103f8")
val NAME_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380002-10bd-4fdb-ba21-1922d6cf860d")


@Suppress("DEPRECATION")
class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice
) {
    val isConnected = MutableStateFlow(false)
    val passwordRead = MutableStateFlow<String?>(null)
    val successfulNameWrites = MutableStateFlow(0)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    val xValues = MutableStateFlow<Float>(0.0F)
    val yValues = MutableStateFlow<Float>(0.0F)

    val xandyvalues = MutableStateFlow<BtMPUData>(BtMPUData(0.0F, 0.0F))

    val notifications = MutableSharedFlow<String>()

    private val callback = object: BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val connected = newState == BluetoothGatt.STATE_CONNECTED
            if (connected) {
                //read the list of services
                services.value = gatt.services
            }
            isConnected.value = connected
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            services.value = gatt.services

            gatt.services.forEach { service ->
                Log.v("bluetooth", "Service" + service.uuid.toString())
                service.characteristics.forEach { characteristic ->
                    Log.v("bluetooth", "Characteristic" + characteristic.uuid.toString())
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if (characteristic.uuid == PASSWORD_CHARACTERISTIC_UUID) {
                passwordRead.value = String(characteristic.value)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (characteristic.uuid == NAME_CHARACTERISTIC_UUID) {
                successfulNameWrites.update { it + 1 }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            //Log.v("bluetooth", characteristic.value.contentToString())
            val notification = characteristic.value.contentToString()
            //Log.v("btMPUData", String(characteristic.value,Charsets.UTF_8))
            //Log.v("notification", notification)
            //notifications.tryEmit(notification)
            val btMPUData = String(characteristic.value,Charsets.UTF_8).toBtMPUData()
            Log.v("btMPUData", btMPUData.toString())
            //xandyvalues.tryEmit(btMPUData)
            xandyvalues.value = btMPUData
        }
    }

    private var gatt: BluetoothGatt? = null

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connect() {
        gatt = bluetoothDevice.connectGatt(context, false, callback)
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverServices() {
        gatt?.discoverServices()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun startReceiving() {
        val service = gatt?.getService(CTF_SERVICE_UUID)
        Log.v("bluetooth", "Service: $service")
        val characteristic = service?.getCharacteristic(PASSWORD_CHARACTERISTIC_UUID)
        Log.v("bluetooth", "Characteristic: $characteristic")
        if (characteristic != null) {
            gatt?.setCharacteristicNotification(characteristic, true)
            Log.v("bluetooth", "Set notification")

            val CLIENT_CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            val desc = characteristic.getDescriptor(CLIENT_CONFIG_DESCRIPTOR)
            desc?.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            gatt?.writeDescriptor(desc)
        }
    }

    fun String.toBtMPUData(): BtMPUData {
        val xValue = substringBeforeLast("#").toFloat()
        val yValue = substringAfter("#").toFloat()

        return BtMPUData(
            xValue = xValue,
            yValue = yValue
        )
    }

    /*
    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun readPassword() {
        val service = gatt?.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(PASSWORD_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            val success = gatt?.readCharacteristic(characteristic)
            Log.v("bluetooth", "Read status: $success")
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeName() {
        val service = gatt?.getService(CTF_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(NAME_CHARACTERISTIC_UUID)
        if (characteristic != null) {
            characteristic.value = "Tom".toByteArray()
            val success = gatt?.writeCharacteristic(characteristic)
            Log.v("bluetooth", "Write status: $success")
        }
    }

     */
}