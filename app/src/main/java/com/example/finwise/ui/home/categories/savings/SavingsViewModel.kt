package com.example.finwise.ui.home.categories.savings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingsViewModel @Inject constructor() : ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onSavingsEvent(event: SavingsEvent) {
        when(event) {
            SavingsEvent.OnAccountBalanceButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.AccountBalanceScreen))
                }
            }
            SavingsEvent.OnCarSavingsButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.CarSavingsScreen))
                }
            }
            SavingsEvent.OnExpenseButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.ExpenseScreen))
                }
            }
            SavingsEvent.OnHomeSavingsButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.HomeSavingsScreen))
                }
            }
            SavingsEvent.OnTravelSavingsButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.TravelSavingsScreen))
                }
            }
            SavingsEvent.OnWeddingSavingsButton -> {
                viewModelScope.launch {
                    sendUiEvent(UiEvent.Navigate(Routes.WeddingSavingsScreen))
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