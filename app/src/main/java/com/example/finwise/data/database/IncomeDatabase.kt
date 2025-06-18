package com.example.finwise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finwise.data.dao.IncomeDao
import com.example.finwise.data.model.income.Income
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Database(entities = [Income::class], version = 2,exportSchema = false)
@Singleton
abstract class IncomeDatabase : RoomDatabase() {

    abstract fun incomeDao(): IncomeDao

    companion object {
        const val DATABASE_NAME = "income_database"

        fun getInstance(@ApplicationContext context: Context): IncomeDatabase {
            return  Room.databaseBuilder(
                    context.applicationContext,
                    IncomeDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                    .build()
            }
        }

}
