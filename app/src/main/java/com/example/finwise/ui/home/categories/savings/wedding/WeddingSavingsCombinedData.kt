package com.example.finwise.ui.home.categories.savings.wedding

import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings

data class WeddingSavingsCombinedData(
    val deposits: List<Savings>,
    val goal: Goal?
)
