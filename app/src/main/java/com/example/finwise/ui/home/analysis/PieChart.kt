package com.example.finwise.ui.home.analysis

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun PieChart(
    pieChartData: PieChartData,
    radiusOuter: Float = 200f,
    chartBarWidth: Float = 20f,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    if (pieChartData.slices.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No expense data for pie chart.",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        return
    }

    var animationTriggered by remember { mutableStateOf(false) }

    // Calculate total angle (360 degrees) for the animation
    val totalAngle = 360f

    // Animate the drawing of the pie chart
    val animatedProgress = animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = animDuration, delayMillis = animDelay),
        label = "pieChartAnimationProgress" // Good practice to add a label for debugging
    )

    LaunchedEffect(key1 = pieChartData) {
        // When pieChartData changes (or on initial composition if pieChartData is not empty),
        // we reset the trigger to false (to ensure animation starts from 0),
        // then immediately set it to true to start the animation to 1.
        animationTriggered = false // Reset to 0f
        animationTriggered = true  // Animate to 1f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFf0faf5))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Expense Distribution",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .size(250.dp) // Fixed size for the chart area
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                var currentStartAngle = 0f

                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    pieChartData.slices.forEach { slice ->
                        val sweepAngle = (slice.percentage / 100) * totalAngle
                        drawArc(
                            color = slice.color,
                            startAngle = currentStartAngle,
                            sweepAngle = sweepAngle * animatedProgress.value, // Animate sweep angle
                            useCenter = false, // For a donut chart, use false
                            topLeft = Offset(center.x - radiusOuter, center.y - radiusOuter),
                            size = Size(radiusOuter * 2, radiusOuter * 2),
                            style = Stroke(chartBarWidth) // For a donut chart, use Stroke
                        )
                        currentStartAngle += sweepAngle
                    }
                }

                // Center Text (Total Expense)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total:",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$${pieChartData.totalExpense.roundToInt()}", // Display total expense
                        fontSize = 24.sp,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Legend
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                pieChartData.slices.forEach { slice ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(16.dp)
                                .background(slice.color, RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${slice.category}: $${slice.value.roundToInt()} (${"%.1f".format(slice.percentage)}%)",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}