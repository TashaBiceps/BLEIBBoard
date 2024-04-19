package com.example.bleibboard.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bleibboard.ui.viewmodels.TestViewModel

@Composable
fun TestScreen() {
    val viewModel : TestViewModel = viewModel()
}