package com.example.bleibboard.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bleibboard.ui.viewmodels.BLEViewModel
import com.example.bleibboard.ui.viewmodels.MenuViewmodel
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction5

@Composable
fun TestScreen(
    viewmodel: BLEViewModel,
    getPath: () -> Path,
    xOffsetState: Float,
    yOffsetState: Float,
    navToResults: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val testUiState by viewmodel.testUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Postural Stability Test",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Center
        )
        Graph(
            getPath = getPath,
            addPoint = viewmodel::addPoint,
            mapRange = viewmodel::mapRange,
            xOffsetState = xOffsetState,
            yOffsetState = yOffsetState
        )
        Text(
            text = "Time Left: ${testUiState.timeLeft}",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Left
        )
        Text(
            text = "Trials Left: ${testUiState.trialsLeft}",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Left
        )
        Text(
            text = "Test Status: ${testUiState.status}",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Left
        )
        Text(
            text = "OSI, APSI, MLSI: ${testUiState.testScore.OSI}, ${testUiState.testScore.APSI}, ${testUiState.testScore.MLSI}",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
            textAlign = TextAlign.Left
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //Initial start test
            if (testUiState.trialsLeft > 0 && testUiState.status == 0) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    onClick = {
                        coroutineScope.launch {
                            viewmodel.startTest()
                        }
                    }
                )
                {
                    Text("Start Test/Trial")
                }

            }
            //Stop test in the middle of trial
            else if (testUiState.status == 1) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    onClick = {
                        coroutineScope.launch {
                            viewmodel.stopTest()
                        }
                    }
                ) {
                    Text("Stop Test")
                }
            }
            //If stopped, can restart trial or quit test
            else if (testUiState.trialsLeft > 0 && testUiState.status == 2) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    onClick = {
                        coroutineScope.launch {
                            viewmodel.restartTrial()
                        }
                    }
                ) {
                    Text("Restart Trail")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    onClick = {
                        coroutineScope.launch {
                            viewmodel.resetTest()
                        }
                    }
                ) {
                    Text("Quit Test")
                }
            }
            //If finished all trials can save test and view results
            else if (testUiState.trialsLeft <= 0 && testUiState.status == 0) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    onClick = {
                    coroutineScope.launch {
                        //viewmodel.saveTest()
                        navToResults()
                    }
                }) {
                    Text("Save and view Results")
                }
            }
        }
    }
}


@Composable
fun Graph(
    getPath : () -> Path,
    mapRange : KFunction5<Float, Float, Float, Float, Float, Float>,
    xOffsetState: Float,
    yOffsetState: Float,
    addPoint : (Float, Float) -> Unit
    ) {

    var boxWidth by remember { mutableStateOf(0f) }
    var boxHeight by remember { mutableStateOf(0f) }

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .onGloballyPositioned { coordinates ->
                    boxWidth = coordinates.size.width.toFloat()
                    boxHeight = coordinates.size.height.toFloat()
                }
        ) {
            val centerX = boxWidth * 0.5f
            val centerY = boxWidth * 0.5f

            val circleRadius = boxWidth * 0.4f

            val pointerX = mapRange(
                xOffsetState * -1f,
                -30f,
                30f,
                centerX - circleRadius,
                centerX + circleRadius
            )

            val pointerY = mapRange(
                yOffsetState,
                -30f,
                30f,
                centerY - circleRadius,
                centerY + circleRadius
            )


            Canvas(modifier = Modifier.fillMaxSize()) {
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
                    path = getPath(),
                    color = Color.Black,
                    style = Stroke(width = 5f, miter = 10f)
                )
            }

            // Red dot as pointer
            RedPointer(pointerX = pointerX, pointerY = pointerY, addPoint = addPoint)
        }
    }
}

@Composable
fun RedPointer(
    pointerX : Float,
    pointerY : Float,
    addPoint : (Float, Float) -> Unit
) {
    Canvas(modifier = Modifier
        .fillMaxSize()
        .graphicsLayer(
            translationX = pointerX,
            translationY = pointerY
        )
        .drawBehind {
            addPoint(pointerX, pointerY)
        }
    ) {
        drawCircle(
            color = Color.Red,
            radius = 15.0f,
            center = Offset(0f, 0f)
        )
    }
}



