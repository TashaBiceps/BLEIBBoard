package com.example.bleibboard.presentation.view

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@SuppressLint("MissingPermission")
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
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
        composable(route = Screens.Permissions.name) {
            PermissionReqScreen {
                allPermissionsGranted = true
                navController.navigate(Screens.Scanner.name)
            }
        }
        composable(route = Screens.Scanner.name) {
            ScannerScreen(navController = navController)
        }
        composable(route = Screens.Device.name) {
            DeviceScreen(navController = navController)
        }
        composable(route = Screens.Test.name) {
            TestScreen(navController = navController)
        }
        composable(route = Screens.Records.name) {
            AthleteDataScreen()
        }
    }
}

enum class Screens() {
    Menu,
    Permissions,
    Scanner,
    Device,
    Test,
    Records
}