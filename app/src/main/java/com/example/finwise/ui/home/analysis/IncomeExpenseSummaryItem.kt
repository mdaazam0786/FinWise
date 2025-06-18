package com.example.finwise.ui.home.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finwise.util.Utils
import kotlin.math.absoluteValue

// Make sure formatCurrency is accessible

@Composable
fun IncomeExpenseSummaryItem(
    label: String,          // "Income" or "Expense"
    amount: Double,         // The actual value
    icon: ImageVector,      // The icon to display (e.g., TrendingUp)
    iconTint: Color,        // The color for the icon and its background tint
    amountColor: Color      // The color for the amount text
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Center items horizontally
        verticalArrangement = Arrangement.Center,       // Center items vertically
        modifier = Modifier.width(IntrinsicSize.Max)    // Try to prevent text wrapping
    ) {
        // Icon with rounded background
        Box(
            modifier = Modifier
                .size(48.dp) // Size of the background
                .clip(RoundedCornerShape(12.dp)) // Rounded square background
                .background(iconTint.copy(alpha = 0.1f)), // Light background based on icon tint
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label, // Accessibility description
                tint = iconTint,            // Use the provided tint color for the icon
                modifier = Modifier.size(24.dp) // Size of the icon itself
            )
        }
        Spacer(modifier = Modifier.height(8.dp)) // Space between icon and label
        // Label Text ("Income" / "Expense")
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp)) // Space between label and amount
        // Amount Text (Formatted Currency)
        Text(
            text = Utils.formatCurrency(amount), // Format amount, ignore sign here
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor, // Use specific color for amount
            maxLines = 1
        )
    }
}