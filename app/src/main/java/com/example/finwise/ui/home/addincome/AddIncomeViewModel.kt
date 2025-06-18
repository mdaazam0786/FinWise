package com.example.finwise.ui.home.addincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.dao.IncomeDao
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddIncomeViewModel @Inject constructor(
    private val incomeDao: IncomeDao
): ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _amount =  MutableStateFlow(Routes.EMPTY_STRING)
    val amount : StateFlow<String> = _amount.asStateFlow()

    private val _title = MutableStateFlow(Routes.EMPTY_STRING)
    val title : StateFlow<String> = _title.asStateFlow()

    fun onAddIncomeEvent(event: AddIncomeEvent) {
        when (event) {
            is AddIncomeEvent.OnTitleChange -> {
                onTitleChange(event.title)
            }

            is AddIncomeEvent.OnAmountChange -> {
                onAmountChange(event.amount)
            }

            is AddIncomeEvent.OnSaveClick -> {
                viewModelScope.launch {
                    if(title.value.isEmpty()){
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "The title can't be empty"
                        ))
                        return@launch
                    }
                    else if(amount.value.isEmpty()){
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "The amount can't be empty"
                        ))
                        return@launch
                    }
                    else{
                        incomeDao.insertIncome(event.income)
                        sendUiEvent(UiEvent.PopBackStack)
                    }

                }
            }
        }
    }

        private fun onTitleChange(title: String) {
            viewModelScope.launch {
                _title.value = title
            }
        }

        private fun onAmountChange(amount: String) {
            viewModelScope.launch {
                _amount.value = amount
            }
        }

        private fun sendUiEvent(event: UiEvent) {
            viewModelScope.launch {
                _uiEvent.send(event)
            }
        }
    }

