package com.example.finwise.ui.home.addSavings

import com.example.finwise.ui.home.addexpense.AddExpenseEvent


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.SavingsDao
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.util.Routes
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class AddSavingsViewModel @Inject constructor(
    private val savingsDao: SavingsDao,
) : ViewModel() {
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _amount =  MutableStateFlow(Routes.EMPTY_STRING)
    val amount : StateFlow<String> = _amount.asStateFlow()

    private val _title = MutableStateFlow(Routes.EMPTY_STRING)
    val title : StateFlow<String> = _title.asStateFlow()


    fun onAddSavingsEvent(event : AddSavingsEvent){
        when(event){
            is AddSavingsEvent.OnAmountChange -> {
                onAmountChange(event.amount)
            }
            is AddSavingsEvent.OnSaveClick -> {
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
                        try {
                            withContext(Dispatchers.IO) {
                                savingsDao.insertSavings(
                                    event.savings
                                )
                            }
                            sendUiEvent(UiEvent.ShowSnackbar("Saving added successfully!"))
                            sendUiEvent(UiEvent.PopBackStack)
                        }catch(e : Exception){
                            Log.e("AddSavingsVM", "Error inserting saving: ${e.message}")
                            sendUiEvent(UiEvent.ShowSnackbar("Error adding saving: ${e.message}"))
                        }

                        sendUiEvent(UiEvent.PopBackStack)
                    }

                }
            }
            is AddSavingsEvent.OnTitleChange -> {
                onTitleChange(event.title)
            }
        }
    }

    private fun onTitleChange(title : String){
        viewModelScope.launch {
            _title.value = title
        }
    }
    private fun onAmountChange(amount : String){
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