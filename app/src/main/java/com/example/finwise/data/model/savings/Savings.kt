package com.example.finwise.data.model.savings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "savings")
data class Savings(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    @ColumnInfo(name = "title")
    val title : String,
    @ColumnInfo(name = "amount")
    val amount : Double,
    @ColumnInfo(name = "date")
    val date : Long,
    @ColumnInfo(name = "category")
    val category : String,
    @ColumnInfo(name = "frequency")
    val frequency : String? = null
)
