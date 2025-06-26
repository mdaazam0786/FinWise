package com.example.finwise.ui.home.categories.savings.car

import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings

data class CarSavingsUiState(
    val carGoal: Goal? = null, // Now holds a Goal object
    val totalAmountSaved: Double = 0.0,
    val progressPercentage: Float = 0f,
    val carDeposits: List<Savings> = emptyList()
)