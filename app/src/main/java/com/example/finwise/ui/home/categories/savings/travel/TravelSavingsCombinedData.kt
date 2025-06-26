package com.example.finwise.ui.home.categories.savings.travel

import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings

data class TravelSavingsCombinedData(
    val deposits: List<Savings>,
    val goal: Goal?
)