package com.example.finwise.data.chartData

data class WeeklySummary(
    val week: String, // Format "YYYY-WW" e.g., "2025-24"
    val totalAmount: Double
)