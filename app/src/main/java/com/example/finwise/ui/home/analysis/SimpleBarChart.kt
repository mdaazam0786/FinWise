package com.example.finwise.ui.home.analysis

import android.content.res.Resources
import android.os.Build
import android.util.Log // Import Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size // Import correct Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.xr.compose.testing.toDp
import com.example.finwise.data.chartData.ChartDataPoint
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.max

// Assume ChartDataPoint, colors (chartAxisLabelColor, chartGridLineColor, etc.)
// and textDark are defined elsewhere and accessible.



// --- Sample Colors (Ensure these are defined) ---
val chartAxisLabelColor = Color.Gray
val chartGridLineColor = Color.LightGray.copy(alpha = 0.5f)
val lightGreenBackground = Color(0xFFE6FAF5) // Example, use your actual color

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SimpleBarChart(
    dataPoints: List<ChartDataPoint>,
    modifier: Modifier = Modifier,
    incomeBarColor: Color,
    expenseBarColor: Color,
    labelColor: Color = chartAxisLabelColor,
    gridLineColor: Color = chartGridLineColor,
    barWidth: Dp = 10.dp,
    barGroupSpacing: Dp = 4.dp // Space between income and expense bar within a group
) {
    // Log incoming data at the start
    Log.d("ChartDebug", "SimpleBarChart: DataPoints received = ${dataPoints.size}")
    if (dataPoints.isNotEmpty()) {
        Log.d("ChartDebug", "First data point: ${dataPoints.first()}")
    }


    if (dataPoints.isEmpty()) {
        Log.d("ChartDebug", "DataPoints list is empty, drawing placeholder.")
        Box(
            modifier = modifier
                .fillMaxWidth() // Ensure placeholder takes width
                .height(180.dp) // Maintain consistent height
                .background(lightGreenBackground), // Use card background or similar
            contentAlignment = Alignment.Center
        ) {
            Text("No data available for this period.", color = labelColor)
        }
        return // Exit if no data
    }

    val density = LocalDensity.current

    // --- Determine Scale ---
    val maxDataValue = remember(dataPoints) {
        // Find the highest absolute value among all income and expenses
        max(
            dataPoints.maxOfOrNull { it.income.absoluteValue } ?: 0.0,
            dataPoints.maxOfOrNull { it.expense.absoluteValue } ?: 0.0
        )
    }
    // Use 15k as the top unless data exceeds it, then round up to nearest 1k
    val yAxisTopValue = remember(maxDataValue) {
        max(15000.0, ceil(maxDataValue / 1000.0) * 1000.0)
            .coerceAtLeast(1000.0) // Ensure scale is at least 1k (prevents 0)
    }
    Log.d("ChartDebug", "MaxDataValue: $maxDataValue, yAxisTopValue: $yAxisTopValue")


    // --- Fixed Y-Axis Definition (based on image) ---
    val yAxisLabels = listOf("15k", "10k", "5k", "1k")
    val yAxisValues = listOf(15000.0, 10000.0, 5000.0, 1000.0)

    // --- Layout Measurements ---
    var parentSizePx by remember { mutableStateOf(Size.Zero) } // Use Compose UI Size
    val bottomLabelHeightDp = 20.dp
    val yAxisLabelWidthDp = 30.dp
    val horizontalPaddingDp = 4.dp // Small padding between Y-axis and first bar group

    val bottomLabelHeightPx = with(density) { bottomLabelHeightDp.toPx() }
    val yAxisLabelWidthPx = with(density) { yAxisLabelWidthDp.toPx() }
    val horizontalPaddingPx = with(density) { horizontalPaddingDp.toPx() }

    // Calculate the actual drawing area height using derivedStateOf for efficiency
    val chartAreaHeight by remember(parentSizePx) {
        derivedStateOf {
            (parentSizePx.height - bottomLabelHeightPx).coerceAtLeast(0f)
        }
    }
    Log.d("ChartDebug", "ParentHeightPx: ${parentSizePx.height}, ChartAreaHeight: $chartAreaHeight")


    Box(
        modifier = modifier
            .fillMaxWidth() // Ensure the Box takes available width
            .height(180.dp) // Set a fixed height for the chart container
            .onGloballyPositioned {
                // Update parent size when layout changes
                parentSizePx = Size(it.size.width.toFloat(), it.size.height.toFloat())
                Log.d("ChartDebug", "Box Positioned: New Size=(${parentSizePx.width}, ${parentSizePx.height}), ChartAreaHeight: $chartAreaHeight")
            }
    ) {

        // --- Draw Grid Lines and Y-Axis Labels ---
        // Only draw if we have a positive drawing area height and valid scale
        if (chartAreaHeight > 1f && yAxisTopValue > 0) { // Use a small threshold > 0

            // Y-Axis Labels (Left side)
            Box( // Use a Box to position labels absolutely within the chart area height
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .width(yAxisLabelWidthDp)
                    .height(chartAreaHeight.toDp()) // Use calculated chart area height
                    .padding(end = 4.dp)
            ) {
                yAxisValues.forEach { value ->
                    // Only draw labels that are within the current scale
                    if (value <= yAxisTopValue) {
                        // Calculate Y position relative to the chart area height
                        val yPosFraction = (1f - (value / yAxisTopValue).toFloat())
                        val yOffsetDp = with(density) { (chartAreaHeight * yPosFraction).toDp() }

                        Text(
                            text = "${(value / 1000).toInt()}k",
                            style = MaterialTheme.typography.labelSmall,
                            color = labelColor,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                // Position the label using offset. Adjust vertical alignment slightly.
                                // Offset by yOffsetDp minus half the approximate text height to center it.
                                .offset(y = yOffsetDp - 6.dp) // Adjust the '- 6.dp' based on font size
                        )
                    }
                }
            }


            // Horizontal Grid Lines (Dotted)
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .matchParentSize() // Takes full Box size
                    .padding(start = yAxisLabelWidthDp) // Start drawing after Y labels
            ) {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                yAxisValues.forEach { value ->
                    // Only draw lines that are within the current scale
                    if (value <= yAxisTopValue) {
                        // Calculate Y position based on the dynamic yAxisTopValue scale
                        val yPos = chartAreaHeight * (1f - (value / yAxisTopValue).toFloat())
                        // Ensure line is drawn within the chart area bounds
                        if (yPos in 0f..chartAreaHeight) {
                            drawLine(
                                color = gridLineColor,
                                start = Offset(horizontalPaddingPx, yPos), // Start after padding
                                end = Offset(size.width, yPos), // Draw across available width
                                strokeWidth = 0.8.dp.toPx(),
                                pathEffect = pathEffect
                            )
                        }
                    }
                }
            }
        } // End Grid/Y-Axis drawing

        if (chartAreaHeight > 1f && yAxisTopValue > 0) {
            Row(
                modifier = Modifier
                    // Align to the TOP of the chart area now, allowing it to grow down
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    // Crucially, its height IS the chart area, bars fill *within* this
                    .height(chartAreaHeight.toDp())
                    .padding(start = yAxisLabelWidthDp + horizontalPaddingDp), // Start after Y labels + padding
                verticalAlignment = Alignment.Bottom, // Bars still grow from bottom *within this Row*
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Log.d("ChartDebug", "Drawing bars row...")
                dataPoints.forEachIndexed { index, point ->
                    // Log.d("ChartDebug", "Bar Group ${index+1} (${point.label}): Income=${point.income}, Expense=${point.expense}")
                    Row(
                        modifier = Modifier
                            .fillMaxHeight() // Bars use this Row's height (chartAreaHeight)
                            .wrapContentWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(barGroupSpacing)
                    ) {
                        Bar(
                            value = point.income,
                            maxValue = yAxisTopValue,
                            color = incomeBarColor,
                            width = barWidth,
                            label = "${point.label}-Income"
                        )
                        Bar(
                            value = point.expense,
                            maxValue = yAxisTopValue,
                            color = expenseBarColor,
                            width = barWidth,
                            label = "${point.label}-Expense"
                        )
                    }
                }
            } // End Bar Row
        }

        // --- Draw X-Axis Labels --- **MODIFIED ALIGNMENT**
        if (chartAreaHeight > 1f) {
            Row(
                modifier = Modifier
                    // Align to the VERY BOTTOM of the parent Box
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(bottomLabelHeightDp) // Use reserved height
                    .padding(start = yAxisLabelWidthDp + horizontalPaddingDp), // Align start with bars
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dataPoints.forEach { point ->
                    Text(
                        text = point.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = labelColor,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = (barWidth * 2 + barGroupSpacing + 4.dp))
                    )
                }
            } // End X-Axis Label Row
        }


        // Fallback message if chart area is too small but parent has size
        if (chartAreaHeight <= 1f && parentSizePx.height > 0) {
            Log.w("ChartDebug", "ChartAreaHeight is zero or negative ($chartAreaHeight). Cannot draw chart elements.")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text("Chart area too small", color = labelColor, style = MaterialTheme.typography.labelSmall)
            }
        }

    } // End Main Box
}


/**
 * Draws a single vertical bar for the chart.
 */
@Composable
fun RowScope.Bar(
    value: Double,
    maxValue: Double,
    color: Color,
    width: Dp,
    label: String // Added label for debug logging
) {
    // Ensure maxValue is positive before calculating fraction
    val barHeightFraction = if (maxValue > 0) {
        (value.absoluteValue / maxValue).toFloat().coerceIn(0f, 1f) // Calculate fraction and cap at 1.0
    } else {
        0f // No height if maxValue is invalid
    }

    Log.d("ChartDebug", "Bar($label): Value=$value, MaxValue=$maxValue, HeightFraction=$barHeightFraction")

    // Only draw if fraction is meaningfully positive
    if (barHeightFraction > 0.001f) {
        Box(
            modifier = Modifier
                .align(Alignment.Bottom) // Ensure bar grows from the bottom
                .width(width)
                .fillMaxHeight(fraction = barHeightFraction) // Apply the calculated fraction
                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                .background(color)
        )
    } else {
        // If fraction is zero or near-zero, draw nothing that takes up vertical space
        // but maintain the width for alignment purposes in the parent Row.
        Spacer(modifier = Modifier.width(width).height(0.dp))
        Log.d("ChartDebug", "Bar($label): Height fraction is near zero, drawing Spacer.")
    }
}