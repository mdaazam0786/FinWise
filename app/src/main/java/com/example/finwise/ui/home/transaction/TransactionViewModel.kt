package com.example.finwise.ui.home.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.IncomeDao
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.model.income.Income
import com.example.finwise.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao
): ViewModel(){
    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val expenses = expenseDao.getAllExpense()
    val incomes = incomeDao.getAllIncome()

    fun onTransactionEvent(event : TransactionEvent){
        when(event){
            TransactionEvent.OnExpenseButton -> {
                viewModelScope.launch {

                }
            }
            TransactionEvent.OnIncomeButton -> {
                viewModelScope.launch {
                    incomes
                }
            }
            TransactionEvent.OnTransactionDetailButton -> TODO()
        }
    }

    fun getALlExpenses(expenses : List<Expense>) : List<Expense> {
        return expenses.sortedByDescending { it.date }
    }

    fun getAllIncomes(incomes : List<Income>) : List<Income> {
        return incomes.sortedByDescending { it.date }

    }
    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}