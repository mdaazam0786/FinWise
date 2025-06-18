package com.example.finwise.ui.home.categories.groceries

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
class GroceriesViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
): ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val expense = expenseDao.getExpenseByCategory("Groceries")

    fun onGroceriesEvent(event: GroceriesEvent){
        when(event){
            GroceriesEvent.OnAccountBalanceButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AccountBalanceScreen))
                }
            }
            GroceriesEvent.OnAddExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AddExpenseScreen))
                }
            }
            GroceriesEvent.OnExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.ExpenseScreen))
                }
            }
            GroceriesEvent.OnGroceryDetailButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.GroceriesCategoryScreen))
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