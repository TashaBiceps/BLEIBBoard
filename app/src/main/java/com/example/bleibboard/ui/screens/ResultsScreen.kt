package com.example.bleibboard.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResultsScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Results Screen",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Center
        )

        /* IN PROGRESS
        Text(
            text = "Test Status: ${testUiState.status}",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Left
        )

         */
    }
}