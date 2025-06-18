package com.example.finwise.ui.home.analysis

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.finwise.data.chartData.TimeFrame

// Make sure TimeFrame enum is imported or accessible

@Composable
fun TimeFrameSelector(
    modifier: Modifier = Modifier,             // Optional modifier for positioning/sizing
    selectedTimeFrame: TimeFrame,              // The currently selected TimeFrame from ViewModel
    onTimeFrameSelected: (TimeFrame) -> Unit,  // Callback to ViewModel when an option is clicked
    backgroundColor: Color,                    // Card background color
    selectedColor: Color,                      // Highlight color for the selected option
    unselectedColor: Color,                    // Text color for unselected options
    selectedTextColor: Color                   // Text color for the selected option
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),        // Rounded corners for the card
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Add shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp), // Padding inside the card
            horizontalArrangement = Arrangement.SpaceBetween, // Space out the buttons
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Loop through all possible TimeFrame values
            TimeFrame.values().forEach { timeFrame ->
                // Create a TimeFrameOption for each value
                TimeFrameOption(
                    // Display the name nicely (e.g., "Daily")
                    text = timeFrame.name.lowercase().replaceFirstChar { it.titlecase() },
                    // Check if this is the currently selected timeframe
                    isSelected = selectedTimeFrame == timeFrame,
                    // When clicked, call the provided callback function
                    onClick = { onTimeFrameSelected(timeFrame) },
                    selectedColor = selectedColor,
                    unselectedColor = unselectedColor,
                    selectedTextColor = selectedTextColor
                )
            }
        }
    }
}