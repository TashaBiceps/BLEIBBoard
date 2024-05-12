package com.example.bleibboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.bleibboard.presentation.view.Screens

class MenuViewmodel : ViewModel() {

    fun TestButtonPressed(navController : NavController) {
        navController.navigate(Screens.Permissions.name)
    }

    fun AthleteRecordsButtonPressed(navController : NavController) {
        navController.navigate(Screens.Records.name)
    }

}