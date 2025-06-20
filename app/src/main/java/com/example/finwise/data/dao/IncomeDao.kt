package com.example.finwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.finwise.data.chartData.DailySummary
import com.example.finwise.data.chartData.MonthlySummary
import com.example.finwise.data.chartData.WeeklySummary
import com.example.finwise.data.chartData.YearlySummary
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.model.income.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM income")
    fun getAllIncome() : Flow<List<Income>>

    @Query("SELECT * FROM income WHERE id = :id")
    fun getIncomeById(id : Int) : Flow<Income>

    @Query("SELECT * FROM income ORDER BY date DESC")  // Newest first
    fun getIncomeOrderedByDateDesc(): Flow<List<Expense>>

    @Insert
    suspend fun insertIncome(income: Income)


    @Query("SELECT * FROM income WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getIncomeBetween(startDate: Long, endDate: Long): Flow<List<Income>>

    @Query("SELECT SUM(amount) FROM income WHERE date BETWEEN :startDate AND :endDate")
    fun getIncomeSumBetween(startDate: Long, endDate: Long): Flow<Double?> // Nullable if no income

    @Query("SELECT SUM(amount) FROM income")
    fun getTotalIncomeSum(): Flow<Double?>

    @Query("SELECT date, SUM(amount) AS totalAmount FROM income GROUP BY date ORDER BY date ASC")
    fun getDailyIncomeSummaries(): Flow<List<DailySummary>>

    // NEW: Get weekly sums
    @Query("SELECT STRFTIME('%Y-%W', date/1000, 'unixepoch') AS week, SUM(amount) AS totalAmount FROM income GROUP BY week ORDER BY week ASC")
    fun getWeeklyIncomeSummaries(): Flow<List<WeeklySummary>>

    // NEW: Get monthly sums
    @Query("SELECT STRFTIME('%Y-%m', date/1000, 'unixepoch') AS month, SUM(amount) AS totalAmount FROM income GROUP BY month ORDER BY month ASC")
    fun getMonthlyIncomeSummaries(): Flow<List<MonthlySummary>>

    // NEW: Get yearly sums
    @Query("SELECT STRFTIME('%Y', date/1000, 'unixepoch') AS year, SUM(amount) AS totalAmount FROM income GROUP BY year ORDER BY year ASC")
    fun getYearlyIncomeSummaries(): Flow<List<YearlySummary>>
}