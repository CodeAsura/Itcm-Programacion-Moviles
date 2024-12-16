package com.example.walmart

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(val route: String, val label: String, val icon: Painter)

enum class UserType {
    NONE, CLIENT, EMPLOYEE
}

enum class Screens {
    LOGIN, CLIENT, PRODUCTS, ADD_PRODUCT, BUY_PRODUCT, STOCK, TRANSACTIONS, ADD_TRANSACTION
}

fun Double.moneyFormat() = toInt()
    .toString()
    .reversed()
    .chunked(3)
    .joinToString(",")
    .reversed() + if (this % 1 > 0) ".${(this % 1).toString().split(".")[1].take(2)}" else ""