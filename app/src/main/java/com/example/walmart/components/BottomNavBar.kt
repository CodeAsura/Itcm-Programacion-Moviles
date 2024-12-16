package com.example.walmart.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.walmart.NavigationItem
import com.example.walmart.R
import com.example.walmart.Screens

@Composable
fun BottomNavBar(navController: androidx.navigation.NavHostController) {
    val items = listOf(
        NavigationItem(
            Screens.PRODUCTS.name,
            "Productos",
            painterResource(R.drawable.ic_products)
        ),
        NavigationItem(
            Screens.TRANSACTIONS.name,
            "Transacciones",
            painterResource(R.drawable.ic_transaction)
        ),
        NavigationItem(
            Screens.STOCK.name,
            "Existencias",
            painterResource(R.drawable.ic_stock)
        ),
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(icon = {
                Icon(
                    item.icon,
                    contentDescription = item.label,
                    modifier = Modifier.size(20.dp)
                )
            },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}