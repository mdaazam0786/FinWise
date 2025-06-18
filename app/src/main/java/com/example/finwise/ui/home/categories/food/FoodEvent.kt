package com.example.finwise.ui.home.categories.food

sealed class FoodEvent {
    object OnAccountBalanceButton : FoodEvent()
    object OnExpenseButton : FoodEvent()
    object OnFoodDetailButton : FoodEvent()
    object OnAddExpenseButton : FoodEvent()
}