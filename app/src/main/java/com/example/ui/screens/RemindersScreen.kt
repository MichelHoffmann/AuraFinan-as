package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.RecurringBillEntity
import com.example.data.local.entity.WalletEntity
import com.example.ui.components.FormatUtils
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSecondary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    bills: List<RecurringBillEntity>,
    categories: List<CategoryEntity>,
    wallets: List<WalletEntity>,
    onBack: () -> Unit,
    onToggleBillPaid: (RecurringBillEntity) -> Unit,
    onDeleteBill: (Long) -> Unit,
    onAddBill: (title: String, amount: Double, dueDay: Int, categoryId: Long, categoryName: String, walletId: Long, walletName: String, type: String) -> Unit,
    onSendNotification: (RecurringBillEntity) -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lembretes Inteligentes",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = PurpleSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_reminder")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Lembrete")
            }
        },
        containerColor = BackgroundDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Lembretes Automáticos",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Receba notificações antes do vencimento e nunca mais pague juros.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            if (bills.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhum lembrete recorrente cadastrado.",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(bills, key = { it.id }) { bill ->
                    RecurringBillCard(
                        bill = bill,
                        onTogglePaid = { onToggleBillPaid(bill) },
                        onDelete = { onDeleteBill(bill.id) },
                        onNotify = { onSendNotification(bill) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showAddDialog) {
        AddBillDialog(
            categories = categories,
            wallets = wallets,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, amount, dueDay, catId, catName, walId, walName, type ->
                onAddBill(title, amount, dueDay, catId, catName, walId, walName, type)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RecurringBillCard(
    bill: RecurringBillEntity,
    onTogglePaid: () -> Unit,
    onDelete: () -> Unit,
    onNotify: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkElevated)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (bill.isPaidForCurrentMonth) IncomeGreen.copy(alpha = 0.2f)
                                else WarningGold.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (bill.isPaidForCurrentMonth) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (bill.isPaidForCurrentMonth) IncomeGreen else WarningGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = bill.title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Todo dia ${bill.dueDay} • ${bill.categoryName}",
                            color = PurpleSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = FormatUtils.formatCurrency(bill.amount),
                    color = if (bill.type == "INCOME") IncomeGreen else ExpenseRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions row: Status badge + Test Notification + Toggle Paid + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paid status badge button
                Button(
                    onClick = onTogglePaid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (bill.isPaidForCurrentMonth) IncomeGreen.copy(alpha = 0.2f) else PurplePrimary.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (bill.isPaidForCurrentMonth) "✓ Pago este mês" else "Pendente (Pagar)",
                        color = if (bill.isPaidForCurrentMonth) IncomeGreen else PurpleSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Send notification trigger
                    IconButton(onClick = onNotify, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Enviar Notificação de Teste",
                            tint = PurplePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Excluir",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillDialog(
    categories: List<CategoryEntity>,
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        amount: Double,
        dueDay: Int,
        categoryId: Long,
        categoryName: String,
        walletId: Long,
        walletName: String,
        type: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDayText by remember { mutableStateOf("10") }
    var type by remember { mutableStateOf("EXPENSE") }

    val filteredCats = categories.filter { it.type == type }
    var selectedCat by remember(filteredCats) { mutableStateOf(filteredCats.firstOrNull()) }
    var selectedWallet by remember(wallets) { mutableStateOf(wallets.firstOrNull()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Lembrete Recorrente", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nome da Conta / Receita") },
                    placeholder = { Text("Ex: Internet Fibra, Netflix, Aluguel") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor (R$)") },
                    placeholder = { Text("0,00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dueDayText,
                    onValueChange = { dueDayText = it },
                    label = { Text("Dia do Vencimento (1 a 31)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Tipo:", color = TextSecondary, fontSize = 13.sp)
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    FilterChip(
                        selected = type == "EXPENSE",
                        onClick = {
                            type = "EXPENSE"
                            selectedCat = categories.firstOrNull { it.type == "EXPENSE" }
                        },
                        label = { Text("Despesa a Pagar") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = type == "INCOME",
                        onClick = {
                            type = "INCOME"
                            selectedCat = categories.firstOrNull { it.type == "INCOME" }
                        },
                        label = { Text("Receita a Receber") }
                    )
                }

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMsg ?: "", color = ExpenseRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.replace(",", ".").toDoubleOrNull()
                    val day = dueDayText.toIntOrNull()
                    if (title.isBlank()) {
                        errorMsg = "Informe o título do lembrete."
                        return@Button
                    }
                    if (amount == null || amount <= 0) {
                        errorMsg = "Informe um valor válido."
                        return@Button
                    }
                    if (day == null || day !in 1..31) {
                        errorMsg = "O dia de vencimento deve estar entre 1 e 31."
                        return@Button
                    }
                    val cat = selectedCat ?: categories.firstOrNull()
                    val wal = selectedWallet ?: wallets.firstOrNull()
                    if (cat == null || wal == null) {
                        errorMsg = "Selecione uma categoria e carteira."
                        return@Button
                    }

                    onConfirm(title.trim(), amount, day, cat.id, cat.name, wal.id, wal.name, type)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("Criar Lembrete")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("Cancelar", color = TextMuted)
            }
        },
        containerColor = SurfaceDark
    )
}
