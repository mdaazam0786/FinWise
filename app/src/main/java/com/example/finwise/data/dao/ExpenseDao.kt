package com.example.finwise.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.finwise.data.chartData.DailySummary
import com.example.finwise.data.chartData.MonthlySummary
import com.example.finwise.data.chartData.WeeklySummary
import com.example.finwise.data.chartData.YearlySummary
import com.example.finwise.data.model.expense.Expense
import com.example.finwise.data.model.transaction.CategoryTotal
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses")
    fun getAllExpense() : Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpenseById(id : Int) : Flow<Expense>

    @Query("SELECT * FROM expenses WHERE category = :category")
    fun getExpenseByCategory(category : String) : Flow<List<Expense>>

    @Query("SELECT * FROM expenses ORDER BY date DESC")  // Newest first
    fun getExpensesOrderedByDateDesc(): Flow<List<Expense>>

    @Query("SELECT category, SUM(amount) AS totalAmount FROM expenses GROUP BY category")
    fun getExpenseTotalsByCategory(): Flow<List<CategoryTotal>>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpenseSumBetween(startDate: Long, endDate: Long): Flow<Double?> // Nullable if no expenses

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenseSum(): Flow<Double?>

    @Query("SELECT date, SUM(amount) AS totalAmount FROM expenses GROUP BY date ORDER BY date ASC")
    fun getDailyExpenseSummaries(): Flow<List<DailySummary>>

    // NEW: Get weekly sums for a given period (e.g., current month's weeks)
    // This query is generalized; you'll filter/process in ViewModel/Repository
    @Query("SELECT STRFTIME('%Y-%W', date/1000, 'unixepoch') AS week, SUM(amount) AS totalAmount FROM expenses GROUP BY week ORDER BY week ASC")
    fun getWeeklyExpenseSummaries(): Flow<List<WeeklySummary>>

    // NEW: Get monthly sums
    @Query("SELECT STRFTIME('%Y-%m', date/1000, 'unixepoch') AS month, SUM(amount) AS totalAmount FROM expenses GROUP BY month ORDER BY month ASC")
    fun getMonthlyExpenseSummaries(): Flow<List<MonthlySummary>>

    // NEW: Get yearly sums
    @Query("SELECT STRFTIME('%Y', date/1000, 'unixepoch') AS year, SUM(amount) AS totalAmount FROM expenses GROUP BY year ORDER BY year ASC")
    fun getYearlyExpenseSummaries(): Flow<List<YearlySummary>>


}