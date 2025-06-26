package com.example.finwise.data.model.goal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String, // e.g., "TRAVEL", "HOME" - matches SavingsType name
    val targetAmount: Double,
    val dateSet: Long = System.currentTimeMillis() // When the goal was set/last updated
)
