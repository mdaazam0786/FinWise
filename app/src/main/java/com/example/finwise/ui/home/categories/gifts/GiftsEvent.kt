package com.example.finwise.ui.home.categories.gifts

import com.example.finwise.ui.home.categories.medicine.MedicineEvent

sealed class GiftsEvent {
    object OnAccountBalanceButton : GiftsEvent()
    object OnExpenseButton : GiftsEvent()
    object OnGiftsDetailButton : GiftsEvent()
    object OnAddExpenseButton : GiftsEvent()
}