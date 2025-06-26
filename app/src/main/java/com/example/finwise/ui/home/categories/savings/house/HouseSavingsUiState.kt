package com.example.finwise.ui.home.categories.savings.house

import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings

data class HouseSavingsUiState (
    val houseGoal: Goal? = null, // Now holds a Goal object
    val totalAmountSaved: Double = 0.0,
    val progressPercentage: Float = 0f,
    val houseDeposits: List<Savings> = emptyList()
)