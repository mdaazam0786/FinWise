package com.example.finwise.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.finwise.data.chartData.ChartDataPoint
import com.example.finwise.data.chartData.DailySummary
import com.example.finwise.data.chartData.MonthlySummary
import com.example.finwise.data.chartData.TimeFrame
import com.example.finwise.data.chartData.WeeklySummary
import com.example.finwise.data.chartData.YearlySummary
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.IncomeDao
import com.example.finwise.data.model.expense.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.* // Use java.time for easier date manipulation
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton // Repository is often a Singleton
class TransactionRepository @Inject constructor(
    private val incomeDao: IncomeDao, // Inject IncomeDao
    private val expenseDao: ExpenseDao // Inject ExpenseDao
) {


    fun getDailyExpenseSummaries(): Flow<List<DailySummary>> {
        return expenseDao.getDailyExpenseSummaries()
    }

    fun getWeeklyExpenseSummaries(): Flow<List<WeeklySummary>> {
        return expenseDao.getWeeklyExpenseSummaries()
    }

    fun getMonthlyExpenseSummaries(): Flow<List<MonthlySummary>> {
        return expenseDao.getMonthlyExpenseSummaries()
    }

    fun getYearlyExpenseSummaries(): Flow<List<YearlySummary>> {
        return expenseDao.getYearlyExpenseSummaries()
    }


    fun getDailyIncomeSummaries(): Flow<List<DailySummary>> {
        return incomeDao.getDailyIncomeSummaries()
    }

    fun getWeeklyIncomeSummaries(): Flow<List<WeeklySummary>> {
        return incomeDao.getWeeklyIncomeSummaries()
    }

    fun getMonthlyIncomeSummaries(): Flow<List<MonthlySummary>> {
        return incomeDao.getMonthlyIncomeSummaries()
    }

    fun getYearlyIncomeSummaries(): Flow<List<YearlySummary>> {
        return incomeDao.getYearlyIncomeSummaries()
    }


}