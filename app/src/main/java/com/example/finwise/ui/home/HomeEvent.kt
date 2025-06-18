package com.example.finwise.ui.home

sealed class HomeEvent {

    object OnAccountBalanceButton : HomeEvent()
    object OnExpenseButton : HomeEvent()

}