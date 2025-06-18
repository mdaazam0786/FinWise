package com.example.finwise.ui.home.categories.category

sealed class CategoryEvent {
    object OnNotificationButton : CategoryEvent()
    object OnAccountBalanceButton : CategoryEvent()
    object OnExpenseButton : CategoryEvent()
    object OnFoodCategoryButton : CategoryEvent()
    object OnTransportCategoryButton : CategoryEvent()
    object OnEntertainmentCategoryButton : CategoryEvent()
    object OnRentCategoryButton : CategoryEvent()
    object OnMedicineCategoryButton : CategoryEvent()
    object OnGroceriesCategoryButton : CategoryEvent()
    object OnGiftsCategoryButton : CategoryEvent()
    object OnSavingsCategoryButton : CategoryEvent()

}