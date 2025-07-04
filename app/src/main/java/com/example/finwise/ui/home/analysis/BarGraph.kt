package com.example.finwise.ui.home.analysis

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round


@SuppressLint("AutoboxingStateCreation")
@Composable
fun BarGraph( // Renamed back to LineGraph
    combinedGraphData: CombinedGraphData,
    height: Dp = 250.dp,
    expenseColor: Color = Color.Blue,
    incomeColor: Color = Color.Green,
    showYAxisLabels: Boolean = true,
    showXAxisLabels: Boolean = true,
    animDuration: Int = 1000, // Animation duration for line graph
    animDelay: Int = 0
) {
    val expenseData = combinedGraphData.expenseData
    val incomeData = combinedGraphData.incomeData

    // NEW: Use an Int counter to force animation re-triggering
    var animationTriggerCounter by remember { androidx.compose.runtime.mutableIntStateOf(0) }

    val animatedProgress by animateFloatAsState(
        // Animate from 0f to 1f when the counter is an odd number (after first trigger)
        // or effectively from 0f to 1f on initial launch.
        targetValue = if (animationTriggerCounter > 0) 1f else 0f,
        animationSpec = tween(durationMillis = animDuration, delayMillis = animDelay),
        label = "lineGraphAnimationProgress"
    )

    // Crucial: LaunchedEffect now observes combinedGraphData, but updates the simple counter.
    LaunchedEffect(key1 = combinedGraphData) {
        // Incrementing the counter forces `animateFloatAsState`'s `targetValue` to change
        // (from 0 to 1, or 1 to 0 if we were toggling, but just > 0 for this style),
        // thereby re-triggering the animation every time new data comes in.
        animationTriggerCounter++
        // If you want a more explicit reset and then animate, you can do:
        // animationTriggerCounter = 0 // Resets to 0, which makes targetValue 0
        // animationTriggerCounter = 1 // Immediately sets targetValue to 1, causing animation
    }

    // Find the overall max value to normalize both lines
    val allValues = expenseData.yValues + incomeData.yValues
    val maxValue = allValues.maxOrNull() ?: 1.0f // Prevent division by zero
    val normalizedMaxValue = if (maxValue == 0.0f) 1.0f else maxValue // If all values are 0, use 1 for normalization

    val density = LocalDensity.current
    remember(density) {
        Paint().apply {
            color = Color.Black.toArgb()
            textAlign = Paint.Align.RIGHT
            textSize = density.run { 12.sp.toPx() }
        }
    }

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val yAxisLabelWidth = 50.dp
    val xAxisHeight = 40.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(height + xAxisHeight)
                .background(Color(0xFFf0faf5))
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Y-Axis Labels
                if (showYAxisLabels) {
                    Column(
                        modifier = Modifier
                            .width(yAxisLabelWidth)
                            .fillMaxHeight()
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val numSegments = 3
                        (numSegments downTo 0).forEach { i ->
                            val labelValue = (normalizedMaxValue / numSegments) * i
                            Text(
                                text = round(labelValue).toString(), // Corrected rounding
                                fontSize = 12.sp,
                                color = Color.Black,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Graph Canvas
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = if (showYAxisLabels) 8.dp else 0.dp)
                ) {
                    val graphWidth = size.width
                    val graphHeight = size.height

                    // Draw horizontal dotted lines for Y-axis scale
                    val numHorizontalLines = 3
                    val yStep = graphHeight / (numHorizontalLines + 1)
                    for (i in 1..numHorizontalLines) {
                        drawLine(
                            start = Offset(0f, graphHeight - i * yStep),
                            end = Offset(graphWidth, graphHeight - i * yStep),
                            color = Color.Gray.copy(alpha = 0.5f),
                            strokeWidth = 1.5f,
                            pathEffect = pathEffect
                        )
                    }

                    // --- Draw Expense Line ---
                    if (expenseData.yValues.isNotEmpty() && expenseData.xValues.size == expenseData.yValues.size) {
                        val expensePath = Path()
                        val xStep = graphWidth / (expenseData.yValues.size - 1).toFloat()

                        expenseData.yValues.forEachIndexed { index, value ->
                            val x = index * xStep
                            val y = graphHeight - (value / normalizedMaxValue) * graphHeight
                            if (index == 0) {
                                expensePath.moveTo(x, y)
                            } else {
                                expensePath.lineTo(x, y)
                            }
                        }
                        val pathMeasure = PathMeasure()
                        pathMeasure.setPath(expensePath, false)
                        val segmentLength = pathMeasure.length * animatedProgress // Use animatedProgress value directly

                        val animatedPath = Path()
                        pathMeasure.getSegment(0f, segmentLength, animatedPath, true)

                        drawPath(
                            path = animatedPath, // Draw the animated segment
                            color = expenseColor,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )
                        // Draw dots for each data point
                        expenseData.yValues.forEachIndexed { index, value ->
                            val x = index * xStep
                            val y = graphHeight - (value / normalizedMaxValue) * graphHeight
                            drawCircle(color = expenseColor, radius = 6f, center = Offset(x, y))
                        }
                    }

                    // --- Draw Income Line ---
                    if (incomeData.yValues.isNotEmpty() && incomeData.xValues.size == incomeData.yValues.size) {
                        val incomePath = Path()
                        val xStep = graphWidth / (incomeData.yValues.size - 1).toFloat()

                        incomeData.yValues.forEachIndexed { index, value ->
                            val x = index * xStep
                            val y = graphHeight - (value / normalizedMaxValue) * graphHeight
                            if (index == 0) {
                                incomePath.moveTo(x, y)
                            } else {
                                incomePath.lineTo(x, y)
                            }
                        }
                        val pathMeasure = PathMeasure()
                        pathMeasure.setPath(incomePath, false)
                        val segmentLength = pathMeasure.length * animatedProgress // Use animatedProgress value directly

                        val animatedPath = Path()
                        pathMeasure.getSegment(0f, segmentLength, animatedPath, true)

                        drawPath(
                            path = animatedPath, // Draw the animated segment
                            color = incomeColor,
                            style = Stroke(width = 4f, cap = StrokeCap.Round)
                        )
                        // Draw dots for each data point
                        incomeData.yValues.forEachIndexed { index, value ->
                            val x = index * xStep
                            val y = graphHeight - (value / normalizedMaxValue) * graphHeight
                            drawCircle(color = incomeColor, radius = 6f, center = Offset(x, y))
                        }
                    }
                }
            }

            // X-Axis Labels
            if (showXAxisLabels && expenseData.xValues.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(xAxisHeight),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showYAxisLabels) {
                        Spacer(modifier = Modifier.width(yAxisLabelWidth))
                    }
                    expenseData.xValues.forEach { label ->
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}