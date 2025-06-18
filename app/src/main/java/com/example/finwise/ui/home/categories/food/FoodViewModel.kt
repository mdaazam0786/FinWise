package com.example.finwise.ui.home.categories.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
) : ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val expense = expenseDao.getExpenseByCategory("Food")

    fun onFoodEvent(event: FoodEvent){
        when(event){
            FoodEvent.OnAccountBalanceButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AccountBalanceScreen))
                }
            }
            FoodEvent.OnAddExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AddExpenseScreen))
                }
            }
            FoodEvent.OnExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.ExpenseScreen))
                }
            }
            FoodEvent.OnFoodDetailButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.FoodDetailScreen))
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