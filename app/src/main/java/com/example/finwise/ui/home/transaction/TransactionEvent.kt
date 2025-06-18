package com.example.finwise.ui.home.transaction

sealed class TransactionEvent {
    object OnIncomeButton : TransactionEvent()
    object OnExpenseButton : TransactionEvent()
    object OnTransactionDetailButton : TransactionEvent()
}