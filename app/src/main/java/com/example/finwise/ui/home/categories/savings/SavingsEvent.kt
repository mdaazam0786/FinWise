package com.example.finwise.ui.home.categories.savings

import com.example.finwise.data.model.savings.Savings

sealed class SavingsEvent {
    object OnAccountBalanceButton : SavingsEvent()
    object OnExpenseButton : SavingsEvent()
    object OnTravelSavingsButton : SavingsEvent()
    object OnHomeSavingsButton : SavingsEvent()
    object OnCarSavingsButton : SavingsEvent()
    object OnWeddingSavingsButton : SavingsEvent()
}