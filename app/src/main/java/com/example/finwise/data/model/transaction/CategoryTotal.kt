package com.example.finwise.data.model.transaction

import androidx.room.ColumnInfo

data class CategoryTotal(
    @ColumnInfo(name = "category") val categoryName: String,
    @ColumnInfo(name = "totalAmount") val totalAmount: Double
)
