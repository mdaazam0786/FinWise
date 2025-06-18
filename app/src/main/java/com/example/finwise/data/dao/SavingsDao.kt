package com.example.finwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.finwise.data.model.savings.Savings
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {

    @Query("SELECT * FROM savings")
    fun getAllSavings() : Flow<List<Savings>>

    @Query("SELECT * FROM savings WHERE id = :id")
    fun getSavingsById(id: Int) : Flow<Savings>

    @Query("SELECT * FROM savings WHERE category = :category")
    fun getSavingsByCategory(category: String) : Flow<List<Savings>>

    @Query("SELECT * FROM savings ORDER BY date DESC")
    fun getSavingsOrderedByDateDesc() : Flow<List<Savings>>

    @Insert
    fun insertSavings(saving: Savings)

}