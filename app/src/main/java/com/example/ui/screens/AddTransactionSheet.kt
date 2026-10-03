package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.WalletEntity
import com.example.ui.components.getFinanceIcon
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSecondary
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceDarkElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionSheet(
    categories: List<CategoryEntity>,
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onSaveTransaction: (
        title: String,
        amount: Double,
        type: String,
        categoryId: Long,
        categoryName: String,
        categoryIcon: String,
        categoryColor: Long,
        walletId: Long,
        walletName: String,
        isRecurring: Boolean
    ) -> Unit,
    onCreateCustomCategory: (name: String, type: String, iconName: String, colorHex: Long) -> Unit
) {
    var type by remember { mutableStateOf("EXPENSE") } // "EXPENSE" or "INCOME"
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }

    val filteredCategories = categories.filter { it.type == type }
    var selectedCategory by remember(filteredCategories) {
        mutableStateOf(filteredCategories.firstOrNull())
    }
    var selectedWallet by remember(wallets) {
        mutableStateOf(wallets.firstOrNull())
    }

    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Novo Lançamento",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fechar", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type Selector: Despesa vs Receita
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDarkElevated)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (type == "EXPENSE") ExpenseRed else Color.Transparent)
                        .clickable {
                            type = "EXPENSE"
                            selectedCategory = categories.firstOrNull { it.type == "EXPENSE" }
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_expense"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Despesa",
                        color = if (type == "EXPENSE") Color.White else TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (type == "INCOME") IncomeGreen else Color.Transparent)
                        .clickable {
                            type = "INCOME"
                            selectedCategory = categories.firstOrNull { it.type == "INCOME" }
                        }
                        .padding(vertical = 10.dp)
                        .testTag("tab_income"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Receita",
                        color = if (type == "INCOME") Color.White else TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    // Accept numbers and comma/dot
                    if (input.isEmpty() || input.matches(Regex("""^\d*([.,]\d{0,2})?$"""))) {
                        amountText = input
                    }
                },
                label = { Text("Valor (R$)") },
                placeholder = { Text("0,00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_amount"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = Color(0x44A855F7),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = PurpleSecondary,
                    unfocusedLabelColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Title / Description Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Descrição / Título") },
                placeholder = { Text(if (type == "EXPENSE") "Ex: Supermercado, Aluguel" else "Ex: Salário, Projeto Extra") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_transaction_title"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = Color(0x44A855F7),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = PurpleSecondary,
                    unfocusedLabelColor = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Category Section with "+ Personalizada"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categoria",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Text(
                    text = "+ Criar Categoria",
                    fontSize = 13.sp,
                    color = PurplePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showNewCategoryDialog = true }
                        .padding(4.dp)
                        .testTag("button_create_custom_category")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                filteredCategories.forEach { category ->
                    val isSelected = selectedCategory?.id == category.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category.name, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = getFinanceIcon(category.iconName),
                                contentDescription = null,
                                tint = Color(category.colorHex),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurplePrimary.copy(alpha = 0.25f),
                            selectedLabelColor = TextPrimary,
                            containerColor = SurfaceDarkElevated,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) PurplePrimary else Color(0x22A855F7),
                            selectedBorderColor = PurplePrimary,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Wallet Section
            Text(
                text = "Carteira / Conta",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                wallets.forEach { wallet ->
                    val isSelected = selectedWallet?.id == wallet.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedWallet = wallet },
                        label = { Text(wallet.name, fontSize = 12.sp) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(Color(wallet.color))
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurplePrimary.copy(alpha = 0.25f),
                            selectedLabelColor = TextPrimary,
                            containerColor = SurfaceDarkElevated,
                            labelColor = TextMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recurring Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDarkElevated)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lançamento Recorrente",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Repete todo mês (Ex: Aluguel, Salário)",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = isRecurring,
                    onCheckedChange = { isRecurring = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PurplePrimary,
                        checkedTrackColor = PurplePrimary.copy(alpha = 0.4f)
                    )
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = ExpenseRed,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val cleanAmountStr = amountText.replace(",", ".").trim()
                    val parsedAmount = cleanAmountStr.toDoubleOrNull()
                    if (parsedAmount == null || parsedAmount <= 0.0) {
                        errorMessage = "Informe um valor válido maior que zero."
                        return@Button
                    }
                    if (title.isBlank()) {
                        errorMessage = "Informe uma descrição para o lançamento."
                        return@Button
                    }
                    val cat = selectedCategory
                    if (cat == null) {
                        errorMessage = "Selecione uma categoria."
                        return@Button
                    }
                    val wal = selectedWallet
                    if (wal == null) {
                        errorMessage = "Selecione uma carteira."
                        return@Button
                    }

                    onSaveTransaction(
                        title.trim(),
                        parsedAmount,
                        type,
                        cat.id,
                        cat.name,
                        cat.iconName,
                        cat.colorHex,
                        wal.id,
                        wal.name,
                        isRecurring
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("button_submit_transaction"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "EXPENSE") PurplePrimary else IncomeGreen
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (type == "EXPENSE") "Salvar Despesa" else "Salvar Receita",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }

    // Modal Dialog to Create Custom Category
    if (showNewCategoryDialog) {
        CreateCustomCategoryDialog(
            defaultType = type,
            onDismiss = { showNewCategoryDialog = false },
            onConfirm = { name, catType, iconName, colorHex ->
                onCreateCustomCategory(name, catType, iconName, colorHex)
                showNewCategoryDialog = false
            }
        )
    }
}

@Composable
fun CreateCustomCategoryDialog(
    defaultType: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, iconName: String, colorHex: Long) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(defaultType) }
    var selectedIcon by remember { mutableStateOf("shopping_bag") }
    var selectedColor by remember { mutableStateOf(0xFFA855F7) }
    var dialogError by remember { mutableStateOf<String?>(null) }

    val availableIcons = listOf(
        "restaurant", "home", "directions_car", "sports_esports",
        "favorite", "school", "attach_money", "work",
        "trending_up", "payments", "shopping_bag"
    )

    val availableColors = listOf(
        0xFFA855F7, 0xFFEC4899, 0xFF3B82F6, 0xFF10B981,
        0xFFF59E0B, 0xFFEF4444, 0xFF06B6D4, 0xFF8B5CF6
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nova Categoria Personalizada",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Categoria") },
                    placeholder = { Text("Ex: Pets, Assinaturas, Cripto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tipo de Categoria:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    FilterChip(
                        selected = selectedType == "EXPENSE",
                        onClick = { selectedType = "EXPENSE" },
                        label = { Text("Despesa") }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterChip(
                        selected = selectedType == "INCOME",
                        onClick = { selectedType = "INCOME" },
                        label = { Text("Receita") }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Escolha um Ícone:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableIcons.take(5).forEach { iconName ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (selectedIcon == iconName) PurplePrimary.copy(alpha = 0.3f) else SurfaceDarkElevated)
                                .border(
                                    width = if (selectedIcon == iconName) 2.dp else 0.dp,
                                    color = PurplePrimary,
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getFinanceIcon(iconName),
                                contentDescription = null,
                                tint = if (selectedIcon == iconName) PurplePrimary else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableIcons.drop(5).forEach { iconName ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (selectedIcon == iconName) PurplePrimary.copy(alpha = 0.3f) else SurfaceDarkElevated)
                                .border(
                                    width = if (selectedIcon == iconName) 2.dp else 0.dp,
                                    color = PurplePrimary,
                                    shape = CircleShape
                                )
                                .clickable { selectedIcon = iconName },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getFinanceIcon(iconName),
                                contentDescription = null,
                                tint = if (selectedIcon == iconName) PurplePrimary else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Cor de Destaque:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableColors.forEach { colorVal ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (selectedColor == colorVal) 2.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = colorVal }
                        )
                    }
                }

                if (dialogError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = dialogError ?: "", color = ExpenseRed, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        dialogError = "Informe o nome da categoria."
                        return@Button
                    }
                    onConfirm(name.trim(), selectedType, selectedIcon, selectedColor)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("Adicionar")
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
