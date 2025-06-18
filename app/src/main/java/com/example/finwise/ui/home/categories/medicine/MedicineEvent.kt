package com.example.finwise.ui.home.categories.medicine


sealed class MedicineEvent {
    object OnAccountBalanceButton : MedicineEvent()
    object OnExpenseButton : MedicineEvent()
    object OnMedicineDetailButton : MedicineEvent()
    object OnAddExpenseButton : MedicineEvent()
}