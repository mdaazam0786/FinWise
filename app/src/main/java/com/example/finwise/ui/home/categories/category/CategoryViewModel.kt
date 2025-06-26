package com.example.finwise.ui.home.categories.category

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
class CategoryViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
) : ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onCategoryEvent(event : CategoryEvent){
        when(event){
            CategoryEvent.OnAccountBalanceButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AccountBalanceScreen))
                }
            }
            CategoryEvent.OnEntertainmentCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.EntertainmentCategoryScreen))
                }
            }
            CategoryEvent.OnExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.ExpenseScreen))
                }
            }
            CategoryEvent.OnFoodCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.FoodCategoryScreen))
                }
            }
            CategoryEvent.OnGiftsCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.GiftsCategoryScreen))
                }
            }
            CategoryEvent.OnGroceriesCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.GroceriesCategoryScreen))
                }
            }
            CategoryEvent.OnMedicineCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.MedicineCategoryScreen))
                }
            }
            CategoryEvent.OnNotificationButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.NotificationScreen))
                }
            }
            CategoryEvent.OnRentCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.RentCategoryScreen))
                }
            }
            CategoryEvent.OnSavingsCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.SavingsCategoryScreen))
                }
            }
            CategoryEvent.OnTransportCategoryButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.TransportCategoryScreen))
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