package com.example.finwise.ui.home.categories.rent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RentViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
): ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val expense = expenseDao.getExpenseByCategory("Rent")

    fun onRentEvent(event: RentEvent){
        when(event){
            RentEvent.OnAccountBalanceButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AccountBalanceScreen))
                }
            }
            RentEvent.OnAddExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AddExpenseScreen))
                }
            }
            RentEvent.OnExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.ExpenseScreen))
                }
            }
            RentEvent.OnRentDetailButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.RentCategoryScreen))
                }
            }
        }
    }
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}