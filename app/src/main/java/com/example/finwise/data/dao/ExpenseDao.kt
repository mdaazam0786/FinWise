package com.example.finwise.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.finwise.data.model.expense.Expense
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

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getExpensesBetween(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getExpenseSumBetween(startDate: Long, endDate: Long): Flow<Double?> // Nullable if no expenses

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenseSum(): Flow<Double?>

}