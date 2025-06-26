package com.example.finwise.data.repository

import com.example.finwise.data.chartData.DailySummary
import com.example.finwise.data.chartData.MonthlySummary
import com.example.finwise.data.chartData.WeeklySummary
import com.example.finwise.data.chartData.YearlySummary
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.GoalDao
import com.example.finwise.data.dao.IncomeDao
import com.example.finwise.data.dao.SavingsDao
import com.example.finwise.data.model.goal.Goal
import com.example.finwise.data.model.savings.Savings
import com.example.finwise.data.model.transaction.CategoryTotal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton // Repository is often a Singleton
class TransactionRepository @Inject constructor(
    private val incomeDao: IncomeDao, // Inject IncomeDao
    private val expenseDao: ExpenseDao, // Inject ExpenseDao
    private val savingsDao: SavingsDao,
    private val goalDao : GoalDao
) {


    fun getExpenseTotalsByCategory(): Flow<List<CategoryTotal>> {
        return expenseDao.getExpenseTotalsByCategory()
    }

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


    fun getAllSavings(): Flow<List<Savings>> = savingsDao.getAllSavings()

    fun getGoalByCategory(categoryName: String): Flow<Goal?> = goalDao.getGoalByCategory(categoryName)
    suspend fun insertGoal(goal: Goal) {
        withContext(Dispatchers.IO) {
            goalDao.insertGoal(goal)
        }
    }
    suspend fun updateGoal(goal: Goal) {
        withContext(Dispatchers.IO) {
            goalDao.updateGoal(goal)
        }
    }

}