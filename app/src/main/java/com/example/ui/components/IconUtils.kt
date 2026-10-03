package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

fun getFinanceIcon(iconName: String): ImageVector {
    return when (iconName) {
        "restaurant" -> Icons.Filled.Restaurant
        "home" -> Icons.Filled.Home
        "directions_car" -> Icons.Filled.DirectionsCar
        "sports_esports" -> Icons.Filled.SportsEsports
        "favorite" -> Icons.Filled.Favorite
        "school" -> Icons.Filled.School
        "attach_money" -> Icons.Filled.AttachMoney
        "work" -> Icons.Filled.Work
        "trending_up" -> Icons.Filled.TrendingUp
        "payments" -> Icons.Filled.Payments
        "account_balance" -> Icons.Filled.AccountBalance
        "savings" -> Icons.Filled.Savings
        "event_repeat" -> Icons.Filled.EventRepeat
        "shopping_bag" -> Icons.Filled.ShoppingBag
        else -> Icons.Filled.MoreHoriz
    }
}
