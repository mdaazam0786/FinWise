package com.example.finwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.dao.GoalDao
import com.example.finwise.data.model.goal.Goal
import dagger.hilt.android.qualifiers.ApplicationContext


@Database(entities = [Goal::class], version = 1, exportSchema = false)
abstract class GoalDatabase : RoomDatabase(){
    abstract fun goalDao(): GoalDao

    companion object {
        const val DATABASE_NAME = "goal_database"

        fun getInstance(@ApplicationContext context: Context): GoalDatabase {
            return  Room.databaseBuilder(
                context.applicationContext,
                GoalDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}