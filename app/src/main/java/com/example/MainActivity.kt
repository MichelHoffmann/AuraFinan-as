package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.AddTransactionSheet
import com.example.ui.screens.GoalsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockScreen
import com.example.ui.screens.MainDrawerContent
import com.example.ui.screens.NavDestination
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.WalletsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: FinanceViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }
    var showAddTransactionSheet by remember { mutableStateOf(false) }

    // State Collection
    val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsStateWithLifecycle()
    val walletsWithBalances by viewModel.walletsWithBalances.collectAsStateWithLifecycle()
    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val monthlyStats by viewModel.monthlyStats.collectAsStateWithLifecycle()
    val allGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val selectedFilterType by viewModel.selectedFilterType.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val allWallets by viewModel.allWallets.collectAsStateWithLifecycle()
    val allRecurringBills by viewModel.allRecurringBills.collectAsStateWithLifecycle()
    val categorySummaries by viewModel.categoryExpenseSummaries.collectAsStateWithLifecycle()

    // Notification Permission Launcher (API 33+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Biometric Security Lock Screen
    if (isAppLocked && isBiometricEnabled) {
        LockScreen(
            userName = userName,
            onUnlocked = { viewModel.unlockApp() }
        )
        return
    }

    // Back handling for navigation
    BackHandler(enabled = drawerState.isOpen || currentDestination != NavDestination.HOME) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (currentDestination != NavDestination.HOME) {
            currentDestination = NavDestination.HOME
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainDrawerContent(
                currentDestination = currentDestination,
                userName = userName,
                userEmail = userEmail,
                isBiometricEnabled = isBiometricEnabled,
                onSelectDestination = { dest ->
                    currentDestination = dest
                    coroutineScope.launch { drawerState.close() }
                },
                onToggleBiometric = { enabled ->
                    viewModel.setBiometricEnabled(enabled)
                },
                onLockApp = {
                    coroutineScope.launch { drawerState.close() }
                    viewModel.lockApp()
                }
            )
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            when (currentDestination) {
                NavDestination.HOME -> {
                    HomeScreen(
                        userName = userName,
                        totalBalance = totalBalance,
                        isBalanceVisible = isBalanceVisible,
                        walletsWithBalances = walletsWithBalances,
                        transactions = filteredTransactions,
                        monthlyStats = monthlyStats,
                        monthlyGoals = allGoals,
                        selectedFilterType = selectedFilterType,
                        onFilterChange = { filter -> viewModel.setFilterType(filter) },
                        onToggleBalanceVisibility = { viewModel.toggleBalanceVisibility() },
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onOpenAddTransaction = { showAddTransactionSheet = true },
                        onDeleteTransaction = { id -> viewModel.deleteTransaction(id) },
                        onUpdateUserName = { newName -> viewModel.updateUserName(newName) },
                        onTriggerSmartReminder = { viewModel.triggerSmartGeneralReminder(context) },
                        onNavigateToGoals = { currentDestination = NavDestination.GOALS },
                        onNavigateToWallets = { currentDestination = NavDestination.WALLETS }
                    )
                }

                NavDestination.REPORTS -> {
                    ReportsScreen(
                        monthlyStats = monthlyStats,
                        categorySummaries = categorySummaries,
                        onBack = { currentDestination = NavDestination.HOME }
                    )
                }

                NavDestination.GOALS -> {
                    GoalsScreen(
                        goals = allGoals,
                        transactions = filteredTransactions,
                        categories = allCategories,
                        onBack = { currentDestination = NavDestination.HOME },
                        onAddGoal = { title, target, catId, catName ->
                            viewModel.addGoal(title, target, catId, catName)
                        },
                        onDeleteGoal = { id -> viewModel.deleteGoal(id) }
                    )
                }

                NavDestination.WALLETS -> {
                    WalletsScreen(
                        totalBalance = totalBalance,
                        walletsWithBalances = walletsWithBalances,
                        onBack = { currentDestination = NavDestination.HOME },
                        onAddWallet = { name, type, color, icon, initialBalance ->
                            viewModel.addWallet(name, type, color, icon, initialBalance)
                        },
                        onDeleteWallet = { id -> viewModel.deleteWallet(id) }
                    )
                }

                NavDestination.REMINDERS -> {
                    RemindersScreen(
                        bills = allRecurringBills,
                        categories = allCategories,
                        wallets = allWallets,
                        onBack = { currentDestination = NavDestination.HOME },
                        onToggleBillPaid = { bill -> viewModel.toggleBillPaid(bill) },
                        onDeleteBill = { id -> viewModel.deleteRecurringBill(id) },
                        onAddBill = { title, amount, dueDay, catId, catName, walId, walName, type ->
                            viewModel.addRecurringBill(title, amount, dueDay, catId, catName, walId, walName, type)
                        },
                        onSendNotification = { bill -> viewModel.sendSmartReminderNotification(context, bill) }
                    )
                }
            }
        }
    }

    // Add Transaction Bottom Sheet
    if (showAddTransactionSheet) {
        AddTransactionSheet(
            categories = allCategories,
            wallets = allWallets,
            onDismiss = { showAddTransactionSheet = false },
            onSaveTransaction = { title, amount, type, catId, catName, catIcon, catColor, walId, walName, isRecurring ->
                viewModel.addTransaction(
                    title = title,
                    amount = amount,
                    type = type,
                    categoryId = catId,
                    categoryName = catName,
                    categoryIcon = catIcon,
                    categoryColor = catColor,
                    walletId = walId,
                    walletName = walName,
                    isRecurring = isRecurring
                )
            },
            onCreateCustomCategory = { name, type, iconName, colorHex ->
                viewModel.addCategory(name, type, iconName, colorHex)
            }
        )
    }
}
