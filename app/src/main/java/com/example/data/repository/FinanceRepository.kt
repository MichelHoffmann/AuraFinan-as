package com.example.data.repository

import com.example.data.local.dao.FinanceDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.MonthlyGoalEntity
import com.example.data.local.entity.RecurringBillEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val dao: FinanceDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allWallets: Flow<List<WalletEntity>> = dao.getAllWallets()
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val allGoals: Flow<List<MonthlyGoalEntity>> = dao.getAllGoals()
    val allRecurringBills: Flow<List<RecurringBillEntity>> = dao.getAllRecurringBills()

    fun getGoalsForMonth(monthYear: String): Flow<List<MonthlyGoalEntity>> =
        dao.getGoalsForMonth(monthYear)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        dao.insertTransaction(transaction)

    suspend fun deleteTransaction(id: Long) =
        dao.deleteTransaction(id)

    suspend fun insertWallet(wallet: WalletEntity): Long =
        dao.insertWallet(wallet)

    suspend fun updateWallet(wallet: WalletEntity) =
        dao.updateWallet(wallet)

    suspend fun deleteWallet(id: Long) =
        dao.deleteWallet(id)

    suspend fun insertCategory(category: CategoryEntity): Long =
        dao.insertCategory(category)

    suspend fun deleteCategory(id: Long) =
        dao.deleteCategory(id)

    suspend fun insertGoal(goal: MonthlyGoalEntity): Long =
        dao.insertGoal(goal)

    suspend fun deleteGoal(id: Long) =
        dao.deleteGoal(id)

    suspend fun insertRecurringBill(bill: RecurringBillEntity): Long =
        dao.insertRecurringBill(bill)

    suspend fun updateRecurringBill(bill: RecurringBillEntity) =
        dao.updateRecurringBill(bill)

    suspend fun deleteRecurringBill(id: Long) =
        dao.deleteRecurringBill(id)
}
