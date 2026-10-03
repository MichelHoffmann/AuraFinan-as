package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "EXPENSE" or "INCOME"
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: Long,
    val walletId: Long,
    val walletName: String,
    val dateMillis: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "CHECKING", "CASH", "SAVINGS", "CREDIT", "INVESTMENT"
    val color: Long,
    val icon: String,
    val initialBalance: Double = 0.0
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "EXPENSE" or "INCOME"
    val iconName: String,
    val colorHex: Long,
    val isCustom: Boolean = false
)

@Entity(tableName = "monthly_goals")
data class MonthlyGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val categoryId: Long? = null,
    val categoryName: String = "",
    val monthYear: String // e.g. "10/2026"
)

@Entity(tableName = "recurring_bills")
data class RecurringBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val dueDay: Int, // 1 to 31
    val categoryId: Long,
    val categoryName: String,
    val walletId: Long,
    val walletName: String,
    val type: String = "EXPENSE",
    val isPaidForCurrentMonth: Boolean = false,
    val reminderDaysBefore: Int = 2
)
