package com.example.finwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finwise.data.dao.ExpenseDao
import com.example.finwise.data.model.expense.Expense
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Database(entities = [Expense::class], version = 2,exportSchema = false)
@Singleton
abstract class ExpenseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        const val DATABASE_NAME = "expense_database"

        fun getInstance(@ApplicationContext context: Context): ExpenseDatabase {
            return  Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                    .build()
            }
        }

}
