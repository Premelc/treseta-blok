package com.premelc.templateproject.uiComponents.graph

import android.graphics.Paint
import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val paddingSpace = 16.dp
private const val VERTICAL_STEP = 5f
private val yValues = (0..10).toList().map { it * 5 }

@Composable
fun Graph(
    modifier: Modifier,
    xValues: List<Int>,
    firstTeamPoints: List<Int>,
    secondTeamPoints: List<Int>,
) {
    val firstTeamCoordinates = mutableListOf<PointF>()
    val secondTeamCoordinates = mutableListOf<PointF>()
    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        contentAlignment = Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize(),
        ) {
            val xAxisSpace = (size.width - paddingSpace.toPx()) / xValues.size
            val yAxisSpace = size.height / yValues.size

            /** placing x axis points */
            for (i in xValues.indices) {
                drawContext.canvas.nativeCanvas.drawText(
                    "${xValues[i]}.",
                    xAxisSpace * (i + 1),
                    size.height,
                    textPaint
                )
                drawLine(
                    start = Offset(xAxisSpace * (i + 1), size.height - 53),
                    end = Offset(xAxisSpace * (i + 1), 0f),
                    color = if (i == 0) Color.Black else Color.LightGray,
                    strokeWidth = if (i == 0) 2.dp.toPx() else 1.dp.toPx()
                )
            }

            /** placing y axis points */
            for (i in yValues.indices) {
                drawContext.canvas.nativeCanvas.drawText(
                    "${yValues[i]}",
                    paddingSpace.toPx() / 2f,
                    (size.height - yAxisSpace * (i + 1)) + 10,
                    textPaint
                )
                drawLine(
                    start = Offset(xAxisSpace, size.height - yAxisSpace * (i + 1)),
                    end = Offset(
                        size.width - paddingSpace.toPx(),
                        size.height - yAxisSpace * (i + 1)
                    ),
                    color = if (i == 0) Color.Black else Color.LightGray,
                    strokeWidth = if (i == 0) 2.dp.toPx() else 1.dp.toPx()
                )
            }


            for (i in firstTeamPoints.indices) {
                val x = (xAxisSpace * xValues[i]) + xAxisSpace
                val y =
                    (size.height - (yAxisSpace * ((firstTeamPoints[i] / VERTICAL_STEP) + 1)))
                firstTeamCoordinates.add(PointF(x, y))
                drawCircle(
                    color = Color.Blue,
                    radius = 7f,
                    center = Offset(x, y)
                )
            }

            for (i in secondTeamPoints.indices) {
                val x = (xAxisSpace * xValues[i]) + xAxisSpace
                val y =
                    (size.height - (yAxisSpace * ((secondTeamPoints[i] / VERTICAL_STEP) + 1)))
                secondTeamCoordinates.add(PointF(x, y))
                drawCircle(
                    color = Color.Red,
                    radius = 7f,
                    center = Offset(x, y)
                )
            }

            drawGraphLine(firstTeamCoordinates, Color.Blue)
            drawGraphLine(secondTeamCoordinates, Color.Red)
        }
    }
}

private fun DrawScope.drawGraphLine(coordinates: MutableList<PointF>, color: Color) {
    /** drawing the path */
    val stroke = Path().apply {
        reset()
        moveTo(coordinates.first().x, coordinates.first().y)
        for (i in 0 until coordinates.size - 1) {
            lineTo(
                coordinates[i + 1].x, coordinates[i + 1].y
            )
        }
    }
    drawPath(
        path = stroke,
        color = color,
        style = Stroke(
            width = 5f,
            cap = StrokeCap.Round
        )
    )
}
