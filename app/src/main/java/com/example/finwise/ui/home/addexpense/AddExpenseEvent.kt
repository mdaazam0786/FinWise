package com.example.finwise.ui.home.addexpense

import com.example.finwise.data.model.expense.Expense

sealed class AddExpenseEvent {
    data class OnAmountChange(val amount : String) : AddExpenseEvent()
    data class OnTitleChange(val title : String) : AddExpenseEvent()
    data class OnSaveClick(val expense : Expense) : AddExpenseEvent()

}