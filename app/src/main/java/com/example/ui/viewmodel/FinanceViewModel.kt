package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.MonthlyGoalEntity
import com.example.data.local.entity.RecurringBillEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.WalletEntity
import com.example.data.notification.NotificationHelper
import com.example.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class WalletWithComputedBalance(
    val wallet: WalletEntity,
    val currentBalance: Double
)

data class CategoryExpenseSummary(
    val categoryName: String,
    val categoryColor: Long,
    val categoryIcon: String,
    val totalAmount: Double,
    val percentage: Float
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = FinanceRepository(database.financeDao())
        NotificationHelper.createNotificationChannel(application)
    }

    // User Profile
    private val _userName = MutableStateFlow("Alexandre Silva")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("alex.silva@email.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    // Privacy & Security
    private val _isBalanceVisible = MutableStateFlow(true)
    val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(true)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isAppLocked = MutableStateFlow(true)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    // Transaction filter
    private val _selectedFilterType = MutableStateFlow("ALL") // "ALL", "EXPENSE", "INCOME"
    val selectedFilterType: StateFlow<String> = _selectedFilterType.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    // Database Flows
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWallets: StateFlow<List<WalletEntity>> = repository.allWallets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGoals: StateFlow<List<MonthlyGoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecurringBills: StateFlow<List<RecurringBillEntity>> = repository.allRecurringBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Wallets with computed current balances
    val walletsWithBalances: StateFlow<List<WalletWithComputedBalance>> = combine(
        allWallets,
        allTransactions
    ) { wallets, transactions ->
        wallets.map { wallet ->
            val income = transactions
                .filter { it.walletId == wallet.id && it.type == "INCOME" }
                .sumOf { it.amount }
            val expense = transactions
                .filter { it.walletId == wallet.id && it.type == "EXPENSE" }
                .sumOf { it.amount }
            val currentBalance = wallet.initialBalance + income - expense
            WalletWithComputedBalance(wallet, currentBalance)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total balance (sum of all wallets)
    val totalBalance: StateFlow<Double> = walletsWithBalances.combine(allWallets) { walletsWithBal, _ ->
        walletsWithBal.sumOf { it.currentBalance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Current Month Income & Expense
    val monthlyStats: StateFlow<Pair<Double, Double>> = allTransactions.combine(allWallets) { transactions, _ ->
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        var totalInc = 0.0
        var totalExp = 0.0

        for (tx in transactions) {
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.dateMillis }
            if (txCal.get(Calendar.MONTH) == currentMonth && txCal.get(Calendar.YEAR) == currentYear) {
                if (tx.type == "INCOME") {
                    totalInc += tx.amount
                } else {
                    totalExp += tx.amount
                }
            }
        }
        Pair(totalInc, totalExp)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(0.0, 0.0))

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _selectedFilterType,
        _selectedCategoryFilter
    ) { transactions, filterType, categoryFilter ->
        transactions.filter { tx ->
            val matchesType = when (filterType) {
                "EXPENSE" -> tx.type == "EXPENSE"
                "INCOME" -> tx.type == "INCOME"
                else -> true
            }
            val matchesCategory = categoryFilter == null || tx.categoryName == categoryFilter
            matchesType && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Category Expense Summaries for Reports
    val categoryExpenseSummaries: StateFlow<List<CategoryExpenseSummary>> = allTransactions.combine(allCategories) { transactions, _ ->
        val expenses = transactions.filter { it.type == "EXPENSE" }
        val totalExpenses = expenses.sumOf { it.amount }
        if (totalExpenses == 0.0) {
            emptyList()
        } else {
            expenses.groupBy { it.categoryName }
                .map { (catName, txList) ->
                    val sum = txList.sumOf { it.amount }
                    val first = txList.first()
                    CategoryExpenseSummary(
                        categoryName = catName,
                        categoryColor = first.categoryColor,
                        categoryIcon = first.categoryIcon,
                        totalAmount = sum,
                        percentage = (sum / totalExpenses).toFloat()
                    )
                }
                .sortedByDescending { it.totalAmount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Actions
    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !_isBalanceVisible.value
    }

    fun setFilterType(type: String) {
        _selectedFilterType.value = type
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun unlockApp() {
        _isAppLocked.value = false
    }

    fun lockApp() {
        if (_isBiometricEnabled.value) {
            _isAppLocked.value = true
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
        if (!enabled) {
            _isAppLocked.value = false
        }
    }

    fun updateUserName(name: String) {
        if (name.isNotBlank()) {
            _userName.value = name.trim()
        }
    }

    // Repository Operations
    fun addTransaction(
        title: String,
        amount: Double,
        type: String,
        categoryId: Long,
        categoryName: String,
        categoryIcon: String,
        categoryColor: Long,
        walletId: Long,
        walletName: String,
        dateMillis: Long = System.currentTimeMillis(),
        isRecurring: Boolean = false,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                title = title.trim(),
                amount = amount,
                type = type,
                categoryId = categoryId,
                categoryName = categoryName,
                categoryIcon = categoryIcon,
                categoryColor = categoryColor,
                walletId = walletId,
                walletName = walletName,
                dateMillis = dateMillis,
                isRecurring = isRecurring,
                notes = notes
            )
            repository.insertTransaction(entity)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun addCategory(name: String, type: String, iconName: String, colorHex: Long) {
        viewModelScope.launch {
            repository.insertCategory(
                CategoryEntity(
                    name = name.trim(),
                    type = type,
                    iconName = iconName,
                    colorHex = colorHex,
                    isCustom = true
                )
            )
        }
    }

    fun addWallet(name: String, type: String, color: Long, icon: String, initialBalance: Double) {
        viewModelScope.launch {
            repository.insertWallet(
                WalletEntity(
                    name = name.trim(),
                    type = type,
                    color = color,
                    icon = icon,
                    initialBalance = initialBalance
                )
            )
        }
    }

    fun deleteWallet(id: Long) {
        viewModelScope.launch {
            repository.deleteWallet(id)
        }
    }

    fun addGoal(title: String, targetAmount: Double, categoryId: Long?, categoryName: String) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val currentMonthYear = "${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
            repository.insertGoal(
                MonthlyGoalEntity(
                    title = title.trim(),
                    targetAmount = targetAmount,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    monthYear = currentMonthYear
                )
            )
        }
    }

    fun deleteGoal(id: Long) {
        viewModelScope.launch {
            repository.deleteGoal(id)
        }
    }

    fun addRecurringBill(
        title: String,
        amount: Double,
        dueDay: Int,
        categoryId: Long,
        categoryName: String,
        walletId: Long,
        walletName: String,
        type: String = "EXPENSE"
    ) {
        viewModelScope.launch {
            repository.insertRecurringBill(
                RecurringBillEntity(
                    title = title.trim(),
                    amount = amount,
                    dueDay = dueDay,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    walletId = walletId,
                    walletName = walletName,
                    type = type,
                    isPaidForCurrentMonth = false
                )
            )
        }
    }

    fun toggleBillPaid(bill: RecurringBillEntity) {
        viewModelScope.launch {
            val updated = bill.copy(isPaidForCurrentMonth = !bill.isPaidForCurrentMonth)
            repository.updateRecurringBill(updated)
            // If marked as paid, record transaction automatically
            if (updated.isPaidForCurrentMonth) {
                repository.insertTransaction(
                    TransactionEntity(
                        title = "Pgto: ${bill.title}",
                        amount = bill.amount,
                        type = bill.type,
                        categoryId = bill.categoryId,
                        categoryName = bill.categoryName,
                        categoryIcon = "event_repeat",
                        categoryColor = 0xFF8B5CF6,
                        walletId = bill.walletId,
                        walletName = bill.walletName,
                        dateMillis = System.currentTimeMillis(),
                        isRecurring = true,
                        notes = "Pagamento recorrente do dia ${bill.dueDay}"
                    )
                )
            }
        }
    }

    fun deleteRecurringBill(id: Long) {
        viewModelScope.launch {
            repository.deleteRecurringBill(id)
        }
    }

    fun sendSmartReminderNotification(context: Context, bill: RecurringBillEntity) {
        NotificationHelper.sendBillReminder(context, bill)
    }

    fun triggerSmartGeneralReminder(context: Context) {
        val pendingBills = allRecurringBills.value.filter { !it.isPaidForCurrentMonth }
        if (pendingBills.isNotEmpty()) {
            val first = pendingBills.first()
            NotificationHelper.sendBillReminder(context, first, "Atenção: Contas pendentes este mês! Vencimento de ${first.title} (R$ ${String.format("%.2f", first.amount)}) no dia ${first.dueDay}.")
        } else {
            NotificationHelper.sendSmartFinancialAlert(
                context,
                "🎉 Tudo em dia!",
                "Parabéns! Todas as suas contas recorrentes deste mês estão quitadas."
            )
        }
    }
}
