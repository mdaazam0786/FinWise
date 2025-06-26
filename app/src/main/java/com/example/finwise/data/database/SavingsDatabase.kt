package com.example.finwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finwise.data.dao.SavingsDao
import com.example.finwise.data.model.savings.Savings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Database(entities = [Savings::class], version = 3, exportSchema = false)
@Singleton
abstract class SavingsDatabase : RoomDatabase(){

    abstract fun savingsDao(): SavingsDao

    companion object{
        const val DATABASE_NAME = "savings_database"

        fun getInstance(@ApplicationContext context: Context) : SavingsDatabase{
            return Room.databaseBuilder(
                context.applicationContext,
                SavingsDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}