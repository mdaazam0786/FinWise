package com.example.finwise.ui.home.analysis

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finwise.R
import com.example.finwise.data.chartData.ChartDataPoint
import com.example.finwise.data.chartData.TimeFrame

// Make sure ChartDataPoint and TimeFrame are imported/accessible

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun IncomeExpenseChartCard(
    modifier: Modifier = Modifier,
    chartData: List<ChartDataPoint>,
    timeFrame: TimeFrame,           // Current timeframe (can be used in title etc.)
    backgroundColor: Color,         // Card background
    incomeBarColor: Color,
    expenseBarColor: Color,
    labelColor: Color,              // Passed down to the chart
    gridLineColor: Color,           // Passed down to the chart
    onSearchClick: () -> Unit = {}, // Callback for search icon click
    onCalendarClick: () -> Unit = {} // Callback for calendar icon click
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp), // Card rounding
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // No shadow
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Padding inside card
            // --- Header Row: Title and Icons ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Push title and icons apart
            ) {
                // Title
                Text(
                    "Income & Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Use theme color
                )
                // Icons (Search, Calendar)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between icons
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colorResource(id = R.color.letters_icons), // Use primary color for icon tint
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape) // Circular background
                            .background(colorResource(id = R.color.main_green).copy(alpha = 0.1f)) // Light circle background
                            .clickable(onClick = onSearchClick) // Make clickable
                            .padding(6.dp) // Padding inside circle
                    )
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendar",
                        tint = colorResource(id = R.color.main_green),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(colorResource(id = R.color.main_green).copy(alpha = 0.1f))
                            .clickable(onClick = onCalendarClick)
                            .padding(6.dp)
                    )
                }
            } // End Header Row

            Spacer(modifier = Modifier.height(16.dp)) // Space between header and chart

            // --- Chart Area ---
            SimpleBarChart( // Use the placeholder (or your real chart)
                modifier = Modifier.fillMaxWidth(), // Chart takes full width
                incomeBarColor = incomeBarColor,
                expenseBarColor = expenseBarColor,
                labelColor = labelColor,
                gridLineColor = gridLineColor,
                dataPoints = chartData
            )

        } // End Column inside Card
    } // End Card
}