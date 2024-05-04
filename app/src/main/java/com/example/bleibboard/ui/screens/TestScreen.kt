package com.example.bleibboard.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bleibboard.ui.viewmodels.BLEViewModel
import com.example.bleibboard.ui.viewmodels.TestViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction5

@Composable
fun TestScreen(
    getPath: () -> Path,
    addPoint : (Float, Float) -> Unit,
    mapRange: KFunction5<Float, Float, Float, Float, Float, Float>,
    xOffsetState: Float,
    yOffsetState: Float,
    startTest: suspend () -> Unit,
    stopTest: () -> Unit,
    resetTest: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Graph(
        getPath = getPath,
        addPoint = addPoint,
        mapRange = mapRange,
        xOffsetState = xOffsetState,
        yOffsetState = yOffsetState
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
            onClick = {
                coroutineScope.launch {
                    startTest()
                }
            })
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



