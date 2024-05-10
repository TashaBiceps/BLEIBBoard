package com.example.bleibboard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bleibboard.Screens

class MenuViewmodel : ViewModel() {

    fun TestButtonPressed(navController : NavController) {
        navController.navigate(Screens.Welcome.name)
    }

    fun AthleteRecordsButtonPressed(navController : NavController) {
        navController.navigate(Screens.Records.name)
    }

}