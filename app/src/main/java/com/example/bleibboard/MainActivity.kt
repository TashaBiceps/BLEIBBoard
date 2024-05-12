package com.example.bleibboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bleibboard.presentation.view.Navigation
import com.example.bleibboard.ui.theme.BLEIBBoardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

/*
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
    viewModel: BLEViewModel = viewModel(),
    recordsViewmodel: TestListViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var allPermissionsGranted by remember {
        mutableStateOf (haveAllPermissions(context))
    }

    NavHost(
        navController = navController,
        startDestination = Screens.Menu.name
    ) {
        composable(route = Screens.Menu.name) {
            MenuScreen(navController = navController)
        }
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
                startTest = { navController.navigate(Screens.Test.name) },
            )
        }
        composable(route = Screens.Test.name) {
            TestScreen(
                viewmodel = viewModel,
                getPath =  viewModel::getPath,
                xOffsetState = uiState.xOffsetState,
                yOffsetState = uiState.yOffsetState,
                navToResults = { navController.navigate(Screens.Records.name) }
            )
        }
        composable(route = Screens.Records.name) {
            AthleteDataScreen(
                viewModel = recordsViewmodel,
                onEvent = recordsViewmodel::onEvent)
        }
    }
}

 */