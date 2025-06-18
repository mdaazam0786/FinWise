package com.example.finwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
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
}