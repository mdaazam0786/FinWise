package com.example.finwise.ui.home.categories.rent

import com.example.finwise.ui.home.categories.medicine.MedicineEvent

sealed class RentEvent {
    object OnAccountBalanceButton : RentEvent()
    object OnExpenseButton : RentEvent()
    object OnRentDetailButton : RentEvent()
    object OnAddExpenseButton : RentEvent()
}