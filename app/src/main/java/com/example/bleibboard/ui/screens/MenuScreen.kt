package com.example.bleibboard.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bleibboard.R
import com.example.bleibboard.ui.viewmodels.MenuViewmodel

@Composable
fun MenuScreen(
    menuViewmodel: MenuViewmodel = viewModel(),
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu_title),
                contentDescription = ""
            )
            MenuButton(R.string.menu_button_test, onClick = { menuViewmodel.TestButtonPressed(navController) })
            MenuButton(R.string.menu_button_athleterecords, onClick = { menuViewmodel.AthleteRecordsButtonPressed(navController) })
        }
    }
}

@Composable
fun MenuButton(
    @StringRes buttonName: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier
            .padding(16.dp)
    ) {
        Text(stringResource(buttonName))
    }
}