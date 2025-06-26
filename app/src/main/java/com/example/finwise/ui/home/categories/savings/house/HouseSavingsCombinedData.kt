package com.example.finwise.ui.home.categories.savings.house

import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings

data class HouseSavingsCombinedData(
    val deposits: List<Savings>,
    val goal: Goal?
)
