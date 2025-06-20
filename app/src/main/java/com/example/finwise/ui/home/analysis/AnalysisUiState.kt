package com.example.finwise.ui.home.analysis

import com.example.finwise.data.chartData.TimeFrame
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.model.income.Income

data class AnalysisUiState(
    val selectedTimeframe: TimeFrame = TimeFrame.DAILY,
    val incomes: List<Income> = emptyList(),
    val expenses: List<Expense> = emptyList()
)