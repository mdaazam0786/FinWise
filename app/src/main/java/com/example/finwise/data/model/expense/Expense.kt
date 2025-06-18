package com.example.finwise.data.model.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "expenses")
data class Expense(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "frequency")
    val frequency: String? = null

)