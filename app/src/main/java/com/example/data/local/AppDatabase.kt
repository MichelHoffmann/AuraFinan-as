package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.FinanceDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.MonthlyGoalEntity
import com.example.data.local.entity.RecurringBillEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.WalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [
        TransactionEntity::class,
        WalletEntity::class,
        CategoryEntity::class,
        MonthlyGoalEntity::class,
        RecurringBillEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aura_financas_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.financeDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: FinanceDao) {
            // Seed Default Wallets
            val wallets = listOf(
                WalletEntity(name = "Nubank Principal", type = "CHECKING", color = 0xFF8A05BE, icon = "account_balance", initialBalance = 4250.80),
                WalletEntity(name = "Carteira Física", type = "CASH", color = 0xFF10B981, icon = "payments", initialBalance = 380.00),
                WalletEntity(name = "Reserva Inter", type = "SAVINGS", color = 0xFFFF7A00, icon = "savings", initialBalance = 12500.00),
                WalletEntity(name = "Investimentos XP", type = "INVESTMENT", color = 0xFF3B82F6, icon = "trending_up", initialBalance = 8400.50)
            )
            dao.insertWallets(wallets)

            // Seed Default Categories
            val defaultCategories = listOf(
                CategoryEntity(name = "Alimentação & Mercado", type = "EXPENSE", iconName = "restaurant", colorHex = 0xFFFF6584, isCustom = false),
                CategoryEntity(name = "Moradia & Contas", type = "EXPENSE", iconName = "home", colorHex = 0xFF8B5CF6, isCustom = false),
                CategoryEntity(name = "Transporte & Combustível", type = "EXPENSE", iconName = "directions_car", colorHex = 0xFF06B6D4, isCustom = false),
                CategoryEntity(name = "Lazer & Entretenimento", type = "EXPENSE", iconName = "sports_esports", colorHex = 0xFFEC4899, isCustom = false),
                CategoryEntity(name = "Saúde & Farmácia", type = "EXPENSE", iconName = "favorite", colorHex = 0xFF10B981, isCustom = false),
                CategoryEntity(name = "Educação & Cursos", type = "EXPENSE", iconName = "school", colorHex = 0xFFF59E0B, isCustom = false),
                CategoryEntity(name = "Salário & Rendimentos", type = "INCOME", iconName = "attach_money", colorHex = 0xFF10B981, isCustom = false),
                CategoryEntity(name = "Freelance & Projetos", type = "INCOME", iconName = "work", colorHex = 0xFF8B5CF6, isCustom = false),
                CategoryEntity(name = "Rendimentos & Dividendos", type = "INCOME", iconName = "trending_up", colorHex = 0xFF3B82F6, isCustom = false),
                CategoryEntity(name = "Outras Despesas", type = "EXPENSE", iconName = "more_horiz", colorHex = 0xFF9CA3AF, isCustom = false)
            )
            dao.insertCategories(defaultCategories)

            // Current Month
            val cal = Calendar.getInstance()
            val currentMonthYear = "${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"

            // Seed Monthly Goals
            val goals = listOf(
                MonthlyGoalEntity(title = "Teto Mensal Geral", targetAmount = 4500.0, categoryId = null, categoryName = "Todas as categorias", monthYear = currentMonthYear),
                MonthlyGoalEntity(title = "Alimentação & Mercado", targetAmount = 1400.0, categoryId = 1, categoryName = "Alimentação & Mercado", monthYear = currentMonthYear),
                MonthlyGoalEntity(title = "Lazer & Streaming", targetAmount = 600.0, categoryId = 4, categoryName = "Lazer & Entretenimento", monthYear = currentMonthYear),
                MonthlyGoalEntity(title = "Transporte & App", targetAmount = 500.0, categoryId = 3, categoryName = "Transporte & Combustível", monthYear = currentMonthYear)
            )
            dao.insertGoals(goals)

            // Seed Recurring Bills
            val recurringBills = listOf(
                RecurringBillEntity(title = "Aluguel & Condomínio", amount = 1650.0, dueDay = 10, categoryId = 2, categoryName = "Moradia & Contas", walletId = 1, walletName = "Nubank Principal", type = "EXPENSE", isPaidForCurrentMonth = true),
                RecurringBillEntity(title = "Internet Fibra Óptica", amount = 119.90, dueDay = 15, categoryId = 2, categoryName = "Moradia & Contas", walletId = 1, walletName = "Nubank Principal", type = "EXPENSE", isPaidForCurrentMonth = false),
                RecurringBillEntity(title = "Netflix & Spotify", amount = 65.80, dueDay = 18, categoryId = 4, categoryName = "Lazer & Entretenimento", walletId = 1, walletName = "Nubank Principal", type = "EXPENSE", isPaidForCurrentMonth = false),
                RecurringBillEntity(title = "Salário Empresa", amount = 7200.0, dueDay = 5, categoryId = 7, categoryName = "Salário & Rendimentos", walletId = 1, walletName = "Nubank Principal", type = "INCOME", isPaidForCurrentMonth = true)
            )
            dao.insertRecurringBills(recurringBills)

            // Seed Recent Transactions
            val now = System.currentTimeMillis()
            val dayMs = 86_400_000L
            val transactions = listOf(
                TransactionEntity(title = "Salário Mensal", amount = 7200.0, type = "INCOME", categoryId = 7, categoryName = "Salário & Rendimentos", categoryIcon = "attach_money", categoryColor = 0xFF10B981, walletId = 1, walletName = "Nubank Principal", dateMillis = now - (dayMs * 1), isRecurring = true),
                TransactionEntity(title = "Supermercado Pão de Açúcar", amount = 342.60, type = "EXPENSE", categoryId = 1, categoryName = "Alimentação & Mercado", categoryIcon = "restaurant", categoryColor = 0xFFFF6584, walletId = 1, walletName = "Nubank Principal", dateMillis = now - (dayMs * 0) - 3600000 * 2),
                TransactionEntity(title = "Uber para Reunião", amount = 28.50, type = "EXPENSE", categoryId = 3, categoryName = "Transporte & Combustível", categoryIcon = "directions_car", categoryColor = 0xFF06B6D4, walletId = 1, walletName = "Nubank Principal", dateMillis = now - (dayMs * 0) - 3600000 * 5),
                TransactionEntity(title = "Aluguel & Condomínio", amount = 1650.0, type = "EXPENSE", categoryId = 2, categoryName = "Moradia & Contas", categoryIcon = "home", categoryColor = 0xFF8B5CF6, walletId = 1, walletName = "Nubank Principal", dateMillis = now - (dayMs * 1)),
                TransactionEntity(title = "Farmácia Droga Raia", amount = 84.90, type = "EXPENSE", categoryId = 5, categoryName = "Saúde & Farmácia", categoryIcon = "favorite", categoryColor = 0xFF10B981, walletId = 2, walletName = "Carteira Física", dateMillis = now - (dayMs * 2)),
                TransactionEntity(title = "Projeto Freelance UI/UX", amount = 1450.0, type = "INCOME", categoryId = 8, categoryName = "Freelance & Projetos", categoryIcon = "work", categoryColor = 0xFF8B5CF6, walletId = 1, walletName = "Nubank Principal", dateMillis = now - (dayMs * 3)),
                TransactionEntity(title = "Jantar Restaurante Japonês", amount = 185.0, type = "EXPENSE", categoryId = 1, categoryName = "Alimentação & Mercado", categoryIcon = "restaurant", categoryColor = 0xFFFF6584, walletId = 1, walletName = "Nubank Principal", dateMillis = now - (dayMs * 4)),
                TransactionEntity(title = "Cinema & Pipoca", amount = 64.0, type = "EXPENSE", categoryId = 4, categoryName = "Lazer & Entretenimento", categoryIcon = "sports_esports", categoryColor = 0xFFEC4899, walletId = 2, walletName = "Carteira Física", dateMillis = now - (dayMs * 5))
            )
            transactions.forEach { dao.insertTransaction(it) }
        }
    }
}
