package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.MonthlyGoalEntity
import com.example.data.local.entity.TransactionEntity
import com.example.ui.components.FormatUtils
import com.example.ui.components.getFinanceIcon
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.PurpleAccentGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSecondary
import com.example.ui.theme.PurpleTertiary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WalletWithComputedBalance

@Composable
fun HomeScreen(
    userName: String,
    totalBalance: Double,
    isBalanceVisible: Boolean,
    walletsWithBalances: List<WalletWithComputedBalance>,
    transactions: List<TransactionEntity>,
    monthlyStats: Pair<Double, Double>,
    monthlyGoals: List<MonthlyGoalEntity>,
    selectedFilterType: String,
    onFilterChange: (String) -> Unit,
    onToggleBalanceVisibility: () -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenAddTransaction: () -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    onUpdateUserName: (String) -> Unit,
    onTriggerSmartReminder: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToWallets: () -> Unit
) {
    val context = LocalContext.current
    var showEditNameDialog by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0814)),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. Top Bar: User Name & Avatar Photo + Drawer & Notification
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Menu Icon + User Avatar & Greeting
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onOpenDrawer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SurfaceDarkElevated)
                                .size(42.dp)
                                .testTag("button_open_drawer")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu Lateral",
                                tint = PurpleSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // User Photo Avatar
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .border(2.dp, PurplePrimary, CircleShape)
                                .clickable { showEditNameDialog = true }
                                .testTag("user_avatar_profile")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.user_avatar),
                                contentDescription = "Foto de Perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.clickable { showEditNameDialog = true }) {
                            Text(
                                text = "Olá,",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = userName,
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Nome",
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // Right: Smart Notification Bell
                    IconButton(
                        onClick = onTriggerSmartReminder,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceDarkElevated)
                            .size(42.dp)
                            .testTag("button_notification_reminder")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Lembrete Inteligente",
                            tint = PurplePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Total Balance Card (Sum of all wallets)
            item {
                TotalBalanceCard(
                    totalBalance = totalBalance,
                    isBalanceVisible = isBalanceVisible,
                    monthlyStats = monthlyStats,
                    onToggleVisibility = onToggleBalanceVisibility
                )
            }

            // 3. Wallets Carousel ("Minhas Carteiras")
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 22.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Minhas Carteiras",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Gerenciar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PurplePrimary,
                            modifier = Modifier
                                .clickable { onNavigateToWallets() }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(walletsWithBalances) { walletItem ->
                            WalletCard(
                                item = walletItem,
                                isBalanceVisible = isBalanceVisible
                            )
                        }
                    }
                }
            }

            // 4. Monthly Budget / Goals Banner
            val globalGoal = monthlyGoals.firstOrNull { it.categoryId == null } ?: monthlyGoals.firstOrNull()
            if (globalGoal != null) {
                item {
                    val spent = monthlyStats.second
                    val target = globalGoal.targetAmount
                    val progress = if (target > 0) (spent / target).coerceIn(0.0, 1.0).toFloat() else 0f
                    val percentage = (progress * 100).toInt()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 18.dp)
                            .clickable { onNavigateToGoals() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = Brush.horizontalGradient(listOf(PurplePrimary.copy(alpha = 0.4f), Color.Transparent))
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Planejamento Mensal",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = globalGoal.title,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "$percentage% da Meta",
                                    color = if (percentage > 85) ExpenseRed else PurpleSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (percentage > 85) ExpenseRed else PurplePrimary,
                                trackColor = Color(0x33A855F7),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Gasto: ${FormatUtils.formatCurrency(spent)}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Teto: ${FormatUtils.formatCurrency(target)}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. Recent Transactions Header & Filter Tabs
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = "Transações Recentes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Tab Row
                    val filterTabs = listOf("ALL" to "Todas", "EXPENSE" to "Despesas", "INCOME" to "Receitas")
                    val selectedTabIndex = filterTabs.indexOfFirst { it.first == selectedFilterType }.coerceAtLeast(0)

                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = SurfaceDarkElevated,
                        contentColor = TextPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp)),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = PurplePrimary
                            )
                        },
                        divider = {}
                    ) {
                        filterTabs.forEachIndexed { index, (key, label) ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { onFilterChange(key) },
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTabIndex == index) PurplePrimary else TextMuted
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // 6. Transactions List grouped by date
            if (transactions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum lançamento encontrado",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                val grouped = transactions.groupBy { FormatUtils.formatDateOnly(it.dateMillis) }
                grouped.forEach { (dateGroup, txList) ->
                    item {
                        Text(
                            text = dateGroup,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 18.dp, bottom = 6.dp)
                        )
                    }

                    items(txList, key = { it.id }) { tx ->
                        TransactionRowItem(
                            transaction = tx,
                            isBalanceVisible = isBalanceVisible,
                            onDelete = { transactionToDelete = tx }
                        )
                    }
                }
            }
        }

        // Floating Action Button to quickly add new transactions
        FloatingActionButton(
            onClick = onOpenAddTransaction,
            containerColor = PurplePrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("fab_add_transaction"),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar Lançamento",
                modifier = Modifier.size(28.dp)
            )
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        var newName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Editar Nome do Usuário", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Seu Nome") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateUserName(newName)
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEditNameDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Delete Transaction Dialog
    transactionToDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Excluir Lançamento", color = TextPrimary) },
            text = {
                Text(
                    text = "Deseja realmente excluir \"${tx.title}\" no valor de ${FormatUtils.formatCurrency(tx.amount)}?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTransaction(tx.id)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                Button(
                    onClick = { transactionToDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun TotalBalanceCard(
    totalBalance: Double,
    isBalanceVisible: Boolean,
    monthlyStats: Pair<Double, Double>,
    onToggleVisibility: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(PurplePrimary.copy(alpha = 0.6f), PurpleAccentGlow.copy(alpha = 0.2f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(PurpleTertiary.copy(alpha = 0.35f), Color.Transparent),
                        radius = 500f
                    )
                )
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saldo Total (Todas as Carteiras)",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(32.dp).testTag("button_toggle_balance_visibility")
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Ocultar/Exibir Saldo",
                        tint = PurpleSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBalanceVisible) FormatUtils.formatCurrency(totalBalance) else "••••••••",
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.testTag("text_total_balance")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Income & Expense Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Receitas
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(IncomeGreen.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(IncomeGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = IncomeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Receitas", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = if (isBalanceVisible) FormatUtils.formatCurrency(monthlyStats.first) else "••••",
                            color = IncomeGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Despesas
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ExpenseRed.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ExpenseRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = ExpenseRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Despesas", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = if (isBalanceVisible) FormatUtils.formatCurrency(monthlyStats.second) else "••••",
                            color = ExpenseRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WalletCard(
    item: WalletWithComputedBalance,
    isBalanceVisible: Boolean
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(115.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(Color(item.wallet.color).copy(alpha = 0.6f), Color.Transparent)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(item.wallet.color).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getFinanceIcon(item.wallet.icon),
                        contentDescription = null,
                        tint = Color(item.wallet.color),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = item.wallet.type,
                    fontSize = 9.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = item.wallet.name,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = if (isBalanceVisible) FormatUtils.formatCurrency(item.currentBalance) else "••••••",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    isBalanceVisible: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category Icon with Color Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(transaction.categoryColor).copy(alpha = 0.18f))
                        .border(1.dp, Color(transaction.categoryColor).copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getFinanceIcon(transaction.categoryIcon),
                        contentDescription = null,
                        tint = Color(transaction.categoryColor),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.categoryName,
                            color = PurpleSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = " • ${transaction.walletName}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                val isExpense = transaction.type == "EXPENSE"
                val sign = if (isExpense) "-" else "+"
                val color = if (isExpense) ExpenseRed else IncomeGreen

                Text(
                    text = if (isBalanceVisible) "$sign ${FormatUtils.formatCurrency(transaction.amount)}" else "••••",
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Excluir",
                        tint = TextMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
