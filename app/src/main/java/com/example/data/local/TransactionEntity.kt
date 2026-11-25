package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val amount: Long,
    val type: TransactionType,
    val category: String,
    val note: String = "",
    val timestamp: Long,
    val createdAt: Long = System.currentTimeMillis()
)
