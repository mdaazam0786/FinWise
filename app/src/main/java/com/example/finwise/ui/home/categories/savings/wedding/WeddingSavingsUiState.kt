package com.example.finwise.ui.home.categories.savings.wedding

import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings

data class WeddingSavingsUiState(
    val weddingGoal: Goal? = null, // Now holds a Goal object
    val totalAmountSaved: Double = 0.0,
    val progressPercentage: Float = 0f,
    val weddingDeposits: List<Savings> = emptyList()
)
