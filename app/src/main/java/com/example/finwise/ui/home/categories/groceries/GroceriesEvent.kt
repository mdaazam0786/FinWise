package com.example.finwise.ui.home.categories.groceries


sealed class GroceriesEvent {
    object OnAccountBalanceButton : GroceriesEvent()
    object OnExpenseButton : GroceriesEvent()
    object OnGroceryDetailButton : GroceriesEvent()
    object OnAddExpenseButton : GroceriesEvent()
}