package com.example.bleibboard

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.bleibboard.ui.screens.BluetoothScreen
import com.example.bleibboard.ui.screens.DeviceScreen
import com.example.bleibboard.ui.screens.TestScreen
import com.example.bleibboard.ui.screens.Welcome
import com.example.bleibboard.ui.screens.haveAllPermissions
import com.example.bleibboard.ui.theme.BLEIBBoardTheme
import com.example.bleibboard.ui.viewmodels.BLEViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BLEIBBoardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}

enum class Screens() {
    Welcome,
    Bluetooth,
    Menu,
    Device,
    Test,
    Records
}

@SuppressLint("MissingPermission")
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    viewModel: BLEViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var allPermissionsGranted by remember {
        mutableStateOf (haveAllPermissions(context))
    }

    NavHost(
        navController = navController,
        startDestination = Screens.Welcome.name
    ) {
        composable(route = Screens.Welcome.name) {
            Welcome {
                allPermissionsGranted = true
                navController.navigate(Screens.Bluetooth.name)
            }
        }
        composable(route = Screens.Bluetooth.name) {
            BluetoothScreen(
                isScanning = uiState.isScanning,
                foundDevices = uiState.foundDevices,
                startScanning = viewModel::startScanning,
                stopScanning = viewModel::stopScanning,
                selectDevice = { device ->
                    viewModel.stopScanning()
                    viewModel.setActiveDevice(device)
                },
                navigateToTest = { navController.navigate(Screens.Device.name) }
            )
        }
        composable(route = Screens.Device.name) {
            DeviceScreen(
                unselectDevice = {
                    viewModel.disconnectActiveDevice()
                    viewModel.setActiveDevice(null)
                    navController.navigate(Screens.Bluetooth.name)
                },
                isDeviceConnected = uiState.isDeviceConnected,
                discoveredCharacteristics = uiState.discoveredCharacteristics,
                connect = viewModel::connectActiveDevice,
                discoverServices = viewModel::discoverActiveDeviceServices,
                startNotifications = viewModel::beginNotificationsFromActiveDevice,
                startTest = { navController.navigate(Screens.Test.name) }
            )
        }
        composable(route = Screens.Test.name) {
            TestScreen(
                getPath = { viewModel.getPath() },
                x = uiState.xOffsetState,
                y = uiState.yOffsetState
            )
        }
    }
}