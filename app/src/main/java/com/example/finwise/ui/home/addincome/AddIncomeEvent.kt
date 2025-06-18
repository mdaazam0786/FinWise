package com.example.finwise.ui.home.addincome

import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.model.income.Income
import com.example.finwise.ui.home.addexpense.AddExpenseEvent

sealed class AddIncomeEvent {
    data class OnAmountChange(val amount : String) : AddIncomeEvent()
    data class OnTitleChange(val title : String) : AddIncomeEvent()
    data class OnSaveClick(val income : Income) : AddIncomeEvent()
}