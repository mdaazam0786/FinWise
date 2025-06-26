package com.example.finwise.ui.home.categories.savings.car

import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings

data class CarSavingsCombinedData(
    val deposits: List<Savings>,
    val goal: Goal?
)
