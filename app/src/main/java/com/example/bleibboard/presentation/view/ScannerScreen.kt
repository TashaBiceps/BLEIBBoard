package com.example.bleibboard.presentation.view

import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.bleibboard.data.remote.ble.PERMISSION_BLUETOOTH_CONNECT
import com.example.bleibboard.data.remote.ble.PERMISSION_BLUETOOTH_SCAN
import com.example.bleibboard.presentation.viewmodel.ScannerViewmodel

@Composable
@RequiresPermission(allOf = [PERMISSION_BLUETOOTH_SCAN, PERMISSION_BLUETOOTH_CONNECT])
fun ScannerScreen(
    navController: NavController
) {

    val viewmodel: ScannerViewmodel = hiltViewModel()
    val scannerState by viewmodel.scannerState.collectAsStateWithLifecycle()

    Column (
        Modifier.padding(horizontal = 10.dp)
    ){
        if (scannerState.isScanning) {
            Text("Scanning...")

            Button(onClick = viewmodel::stopScanning) {
                Text("Stop Scanning")
            }
        }
        else {
            Button(onClick = viewmodel::startScanning) {
                Text("Start Scanning")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(scannerState.foundDevices) { device ->
                DeviceItem(
                    deviceName = device.name,
                    selectDevice = { viewmodel.setActiveDevice(device); viewmodel.navigateToDevice(navController) }
                )
            }
        }
    }
}

@Composable
fun DeviceItem(
    deviceName: String?,
    selectDevice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = deviceName ?: "[Unnamed]",
                textAlign = TextAlign.Center,
            )
            Button(onClick =  selectDevice) {
                Text("Connect")
            }
        }
    }
}