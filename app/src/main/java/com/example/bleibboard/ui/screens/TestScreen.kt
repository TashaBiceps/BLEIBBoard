package com.example.bleibboard.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bleibboard.ui.viewmodels.BLEViewModel
import com.example.bleibboard.ui.viewmodels.TestViewModel
import kotlinx.coroutines.launch

@Composable
fun TestScreen(
    getPath : () -> Path,
    x: Float,
    y: Float,
    startTest: () -> Unit,
    stopTest: () -> Unit,
    resetTest: () -> Unit
) {
    Graph(
        getPath,
        x,
        y
    )
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            onClick = startTest)
        {
            Text("Start")
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            onClick = stopTest)
        {
            Text("Stop")
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(10.dp),
            onClick = resetTest)
        {
            Text("Reset")
        }
    }
}

@Composable
fun Graph(
    getPath : () -> Path,
    xOffsetState: Float,
    yOffsetState: Float
    ) {

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                // Modifier that uses gesture of any drag as the input of the pointer and path
                /*
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val offset = change.position
                        viewModel.addPoint(offset.x, offset.y)
                    }
                }

                 */
                .drawBehind {
                    // Everything else is the graph
                    drawCircle(
                        color = Color.Black,
                        radius = size.width * 0.4f,
                        style = Stroke(width = 10f)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = size.width * 0.3f,
                        style = Stroke(width = 10f)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = size.width * 0.2f,
                        style = Stroke(width = 10f)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = size.width * 0.1f,
                        style = Stroke(width = 10f)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = size.width * 0.02f
                    )
                    drawLine(
                        color = Color.Black,
                        start = Offset(size.width * 0.5f, size.height * 0.1f),
                        end = Offset(size.width * 0.5f, size.height * 0.9f),
                        strokeWidth = 10f
                    )
                    drawLine(
                        color = Color.Black,
                        start = Offset(size.width * 0.1f, size.height * 0.5f),
                        end = Offset(size.width * 0.9f, size.height * 0.5f),
                        strokeWidth = 10f
                    )

                    // Line following the pointer
                    drawPath(
                        getPath(),
                        color = Color.Black,
                        style = Stroke(width = 5f, miter = 10f)
                    )
                    // Red dot as pointer
                    drawCircle(
                        color = Color.Red,
                        radius = 15.0f,
                        center = Offset(xOffsetState, yOffsetState)
                    )
                }
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
        ) {/*
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                onClick = { viewModel.updateTestState(true) })
            {
                Text("Start")
            }

            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                onClick = { viewModel.updateTestState(false) })
            {
                Text("Stop")
            } */
        }
    }
}