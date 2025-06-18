package com.example.finwise.ui.home.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TimeFrameOption(
    text: String,          // Text to display (e.g., "Daily")
    isSelected: Boolean,   // Is this option currently selected?
    onClick: () -> Unit,   // Function to call when clicked
    selectedColor: Color,  // Background color when selected
    unselectedColor: Color,// Text color when not selected
    selectedTextColor: Color // Text color when selected
) {
    // Determine background and text color based on selection state
    val backgroundColor = if (isSelected) selectedColor else Color.Transparent
    val textColor = if (isSelected) selectedTextColor else unselectedColor

    Box(
        modifier = Modifier
            // Clip the background to be rounded
            .clip(RoundedCornerShape(16.dp))
            // Set the background color
            .background(backgroundColor)
            // Make it clickable
            .clickable(onClick = onClick)
            // Add padding inside the button for text
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center // Center the text inside the Box
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, // Bold if selected
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}