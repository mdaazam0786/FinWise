package com.example.finwise.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.finwise.data.model.goal.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) // REPLACE existing if category matches
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE category = :categoryName LIMIT 1")
    fun getGoalByCategory(categoryName: String): Flow<Goal?>

    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<Goal>> // Potentially useful later
}