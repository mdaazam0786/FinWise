package com.example.finwise.data.model.income

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "amount")
    val amount : Double,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "frequency")
    val frequency: String? = null
)