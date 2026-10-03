package com.example.ui.components

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
    private val ptBrLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)

    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun formatDate(millis: Long): String {
        val sdf = SimpleDateFormat("dd 'de' MMM, HH:mm", ptBrLocale)
        return sdf.format(Date(millis))
    }

    fun formatDateOnly(millis: Long): String {
        val today = SimpleDateFormat("yyyyMMdd", ptBrLocale).format(Date())
        val txDate = SimpleDateFormat("yyyyMMdd", ptBrLocale).format(Date(millis))
        return when (txDate) {
            today -> "Hoje"
            else -> SimpleDateFormat("dd 'de' MMMM", ptBrLocale).format(Date(millis))
        }
    }
}
