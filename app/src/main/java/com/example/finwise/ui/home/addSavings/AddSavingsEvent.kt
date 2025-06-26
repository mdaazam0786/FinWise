package com.example.finwise.ui.home.addSavings

import com.example.finwise.data.model.savings.Savings

sealed class AddSavingsEvent {
    data class OnAmountChange(val amount : String) : AddSavingsEvent()
    data class OnTitleChange(val title : String) : AddSavingsEvent()
    data class OnSaveClick(val savings : Savings) : AddSavingsEvent()

}