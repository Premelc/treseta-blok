package com.premelc.tresetacounter.uiComponents.graph

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val paddingSpace = 16.dp
private const val VERTICAL_STEP = 5f

@Composable
@Suppress("MagicNumber", "CyclomaticComplexMethod")
fun Graph(
    modifier: Modifier,
    xValues: List<Int>,
    firstTeamPoints: List<Int>,
    secondTeamPoints: List<Int>,
    yValues: List<Int>,
) {
    val density = LocalDensity.current
    val textColor = if (isSystemInDarkTheme()) WHITE else BLACK
    val graphAxisColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val textPaint = remember(density) {
        Paint().apply {
            color = textColor
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
                if (i != 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${xValues[i]}.",
                        xAxisSpace * (i + 1),
                        size.height,
                        textPaint
                    )
                }
                drawLine(
                    start = Offset(xAxisSpace * (i + 1), size.height - 53),
                    end = Offset(xAxisSpace * (i + 1), 0f),
                    color = if (i == 0) graphAxisColor else Color.LightGray,
                    alpha = if (i == 0) 0.9f else 0.3f,
                    strokeWidth = 1.dp.toPx()
                )
            }

            /** placing y axis points */
            for (i in yValues.indices) {
                if (i % 2 == 0) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${yValues[i]}",
                        paddingSpace.toPx() / 2f,
                        (size.height - yAxisSpace * (i + 1)) + 10,
                        textPaint
                    )
                }
                drawLine(
                    start = Offset(xAxisSpace + 2, size.height - yAxisSpace * (i + 1)),
                    end = Offset(
                        size.width - paddingSpace.toPx(),
                        size.height - yAxisSpace * (i + 1)
                    ),
                    color = if (i == 0) graphAxisColor else Color.LightGray,
                    alpha = if (i == 0) 0.9f else 0.3f,
                    strokeWidth = 1.dp.toPx()
                )
            }

            // this is done to draw the current winning team points over the losing team
            val points = if (firstTeamPoints.sum() >= secondTeamPoints.sum()) {
                Pair(
                    firstTeamPoints,
                    secondTeamPoints
                )
            } else {
                Pair(secondTeamPoints, firstTeamPoints)
            }

            val firstTeamCoordinates = mutableListOf<PointF>()
            val secondTeamCoordinates = mutableListOf<PointF>()

            for (i in points.second.indices) {
                val x = (xAxisSpace * xValues[i]) + xAxisSpace
                val y = (size.height - (yAxisSpace * ((firstTeamPoints[i] / VERTICAL_STEP) + 1)))
                firstTeamCoordinates.add(PointF(x, y))
                drawCircle(
                    color = Color.Blue,
                    radius = 7f,
                    center = Offset(x, y)
                )
            }

            for (i in points.first.indices) {
                val x = (xAxisSpace * xValues[i]) + xAxisSpace
                val y = (size.height - (yAxisSpace * ((secondTeamPoints[i] / VERTICAL_STEP) + 1)))
                secondTeamCoordinates.add(PointF(x, y))
                drawCircle(
                    color = Color.Red,
                    radius = 7f,
                    center = Offset(x, y)
                )
            }

            if (firstTeamPoints.sum() >= secondTeamPoints.sum()) {
                drawGraphLine(secondTeamCoordinates.distinct(), Color.Red)
                drawGraphLine(firstTeamCoordinates.distinct(), Color.Blue)
            } else {
                drawGraphLine(firstTeamCoordinates.distinct(), Color.Blue)
                drawGraphLine(secondTeamCoordinates.distinct(), Color.Red)
            }
        }
    }
}

private fun DrawScope.drawGraphLine(coordinates: List<PointF>, color: Color) {
    /** drawing the path */
    val stroke = Path().apply {
        moveTo(coordinates.first().x, coordinates.first().y)
        for (coord in coordinates) {
            lineTo(coord.x, coord.y)
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
