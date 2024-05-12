package com.example.bleibboard.data.remote.ble


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
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID

val CTF_SERVICE_UUID: UUID = UUID.fromString("ccc82ba1-72c8-49ba-b712-2e49bad52eef") //Service UUID of gyroscope data from IBBoard
val GYROSCOPE_CHARACTERISTIC_UUID: UUID = UUID.fromString("27501e93-1513-4074-866e-6bc3580103f8") // Characteristic UUID of gyroscope data from IBBoard

@Suppress("DEPRECATION")
class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice
) {
    val isConnected = MutableStateFlow(false)
    val passwordRead = MutableStateFlow<String?>(null)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    val xandyvalues = MutableStateFlow<BtMPUData>(BtMPUData(0.0F, 0.0F))

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
            if (characteristic.uuid == GYROSCOPE_CHARACTERISTIC_UUID) {
                passwordRead.value = String(characteristic.value)
            }
        }


        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
            val notification = characteristic.value.contentToString()
            val btMPUData = String(characteristic.value,Charsets.UTF_8).toBtMPUData()
            Log.v("btMPUData", btMPUData.toString())
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
        val characteristic = service?.getCharacteristic(GYROSCOPE_CHARACTERISTIC_UUID)
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
}