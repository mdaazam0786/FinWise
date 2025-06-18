package com.example.finwise.ui.home.categories.entertainment

import com.example.finwise.ui.home.categories.food.FoodEvent

sealed class EntertainmentEvent {
    object OnAccountBalanceButton : EntertainmentEvent()
    object OnExpenseButton : EntertainmentEvent()
    object OnEntertainmentDetailButton : EntertainmentEvent()
    object OnAddExpenseButton : EntertainmentEvent()
}