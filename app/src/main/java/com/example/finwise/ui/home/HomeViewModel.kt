package com.example.finwise.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.IncomeDao
import com.example.finwise.data.model.transaction.Transaction
import com.example.finwise.data.model.transaction.TransactionType
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.model.income.Income
import com.example.finwise.util.UiEvent
import com.example.finwise.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    expenseDao: ExpenseDao,
    incomeDao : IncomeDao
) : ViewModel() {

    private val _uiEvent =  Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val expenses = expenseDao.getAllExpense()

    val incomes = incomeDao.getAllIncome()


    fun getTotalBalance(expenseList : List<Expense>, incomeList : List<Income>) : String{
        var balance = 4100.0

        for(expense in expenseList){
            balance -= expense.amount
        }
        for(income in incomeList){
            balance += income.amount
        }

        return Utils.formatCurrency(balance)
    }

    fun totalExpense(expenseList : List<Expense>) : String{
        var expenses = 0.0

        for (expense in expenseList){
            expenses += expense.amount

        }

        return Utils.formatCurrency(expenses)
    }

    fun totalIncome(incomeList: List<Income>) : String{
        var totalIncome = 0.0

        for (income in incomeList){
            totalIncome += income.amount
        }

        return Utils.formatCurrency(totalIncome)
    }

    fun getAllTransactions(expenseList: List<Expense>, incomeList: List<Income>) : List<Transaction>{
        val transactionList = mutableListOf<Transaction>()

        for (income in incomeList) {
            transactionList.add(
                Transaction(
                    id = income.id,
                    title = income.title,
                    amount = income.amount,
                    date = income.date,
                    type = TransactionType.INCOME,
                    category = income.category
                )
            )
        }

        for (expense in expenseList) {
            transactionList.add(
                Transaction(
                    id = expense.id!!,
                    title = expense.title,
                    amount = expense.amount,
                    date = expense.date,
                    type = TransactionType.EXPENSE,
                    category = expense.category
                )
            )
        }

        return transactionList.sortedByDescending { it.date }

    }

}