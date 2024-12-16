package com.example.walmart.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walmart.api.model.Product
import com.example.walmart.api.model.Storage
import com.example.walmart.api.model.Transaction
import com.google.gson.internal.LinkedTreeMap

@Composable
fun TransactionScreen(
    transactions: List<Transaction>,
    storages: List<Storage>,
    products: List<Product>,
    startDate: String,
    endDate: String,
    onDateChange: (String, String) -> Unit,
    onChangeToAddTransaction: () -> Unit
) {
    var expandedStorage by remember { mutableStateOf(false) }
    var expandedProduct by remember { mutableStateOf(false) }
    var selectedStorage by remember { mutableStateOf<Storage?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val filteredTransactions = transactions.filter { transaction ->
        (selectedStorage == null || transaction.storage.id == selectedStorage?.id) &&
                (selectedProduct == null || transaction.product.id == selectedProduct?.id)
    }.sortedByDescending { it.createdAt }

    val storagesWithTransactions = storages.filter { storage ->
        transactions.any { it.storage.id == storage.id }
    }
    val productsWithTransactions = products.filter { product ->
        transactions.any { it.product.id == product.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expandedStorage = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = selectedStorage?.name ?: "Todos los almacenes")
            }
            DropdownMenu(
                expanded = expandedStorage,
                onDismissRequest = { expandedStorage = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Todos los almacenes") },
                    onClick = {
                        expandedStorage = false
                        selectedStorage = null
                    }
                )
                storagesWithTransactions.forEach { storage ->
                    DropdownMenuItem(
                        text = { Text(storage.name) },
                        onClick = {
                            expandedStorage = false
                            selectedStorage = storage
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { expandedProduct = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = selectedProduct?.name ?: "Todos los productos")
            }
            DropdownMenu(
                expanded = expandedProduct,
                onDismissRequest = { expandedProduct = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Todos los productos") },
                    onClick = {
                        expandedProduct = false
                        selectedProduct = null
                    }
                )
                productsWithTransactions.forEach { product ->
                    DropdownMenuItem(
                        text = { Text(product.name) },
                        onClick = {
                            expandedProduct = false
                            selectedProduct = product
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(
                value = startDate,
                onValueChange = { onDateChange(it, endDate) },
                label = { Text("Fecha de Inicio") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = endDate,
                onValueChange = { onDateChange(startDate, it) },
                label = { Text("Fecha de Fin") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onChangeToAddTransaction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir transacción")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredTransactions) { transaction ->
                TransactionItem(
                    transaction = transaction
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val originDestinyText = when (transaction.originDestiny) {
        is LinkedTreeMap<*, *> -> transaction.originDestiny["name"]?.toString()
        else -> "Error"
    }
    val originDestinyName = when (transaction.type) {
        "IN" -> "Origen"
        "OUT" -> "Destino"
        else -> ""
    }
    val type = when (transaction.type) {
        "IN" -> "Entrada"
        "OUT" -> "Salida"
        else -> ""
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Fecha: ${transaction.createdAt.substring(0, 10)}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "ID: ${transaction.id}",
                style = MaterialTheme.typography.titleLarge
            )
            Text(text = "Tipo: $type", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Producto: ${transaction.product.name}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Cantidad: ${transaction.quantity}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Almacén: ${transaction.storage.name}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "$originDestinyName: $originDestinyText",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
