package com.example.bleibboard.presentation.view

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.bleibboard.data.remote.ble.CTF_SERVICE_UUID
import com.example.bleibboard.presentation.viewmodel.ActiveDeviceViewmodel
import com.example.bleibboard.presentation.viewmodel.ScannerViewmodel

@SuppressLint("MissingPermission")
@Composable
fun DeviceScreen(
    navController: NavController
) {
    val viewmodel: ActiveDeviceViewmodel = hiltViewModel()
    val viewmodelScanner: ScannerViewmodel = hiltViewModel()
    val activeDeviceState by viewmodel.activeDeviceState.collectAsStateWithLifecycle()

    val foundTargetService = activeDeviceState.activeDeviceServices.contains(CTF_SERVICE_UUID.toString())

    Column(
        Modifier.scrollable(rememberScrollState(), Orientation.Vertical)
    ) {
        Button(onClick = viewmodel::connectActiveDevice) {
            Text("1. Connect")
        }
        Text("Device connected: $activeDeviceState.isDeviceConnected")
        Button(onClick = viewmodel::discoverActiveDeviceServices, enabled = activeDeviceState.isDeviceConnected) {
            Text("2. Discover Services")
        }
        LazyColumn {
            items(activeDeviceState.activeDeviceServices.keys.sorted()) { serviceUuid ->
                Text(text = serviceUuid, fontWeight = FontWeight.Black)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    activeDeviceState.activeDeviceServices[serviceUuid]?.forEach {
                        Text(it)
                    }
                }
            }
        }
        Button(onClick = viewmodel::beginNotificationsFromActiveDevice) {
            Text("3. Begin notifications")
        }

        Button(onClick = { viewmodel.navigateToTest(navController) } ) {
            Text("4. Begin test")
        }

        OutlinedButton(
            modifier = Modifier.padding(top = 40.dp),
            onClick = {
                viewmodel.disconnectActiveDevice()
                viewmodelScanner.setActiveDevice(null)
                viewmodel.navigateBack(navController)
            }
        ) {
            Text("Disconnect")
        }
    }
}