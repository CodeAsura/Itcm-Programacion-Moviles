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
import com.example.walmart.api.model.Stock

@Composable
fun StockScreen(
    stocks: List<Stock>,
    storages: List<Storage>,
    products: List<Product>
) {
    var expandedStorage by remember { mutableStateOf(false) }
    var expandedProduct by remember { mutableStateOf(false) }
    var selectedStorage by remember { mutableStateOf<Storage?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val filteredStocks = stocks.filter { stock ->
        (selectedStorage == null || stock.storage.id == selectedStorage?.id) &&
                (selectedProduct == null || stock.stock.any { it.product.id == selectedProduct?.id })
    }

    val storagesWithStocks = storages.filter { storage ->
        stocks.any { it.storage.id == storage.id }
    }

    val productsWithStocks = products.filter { product ->
        stocks.any { it.stock.any { it.product.id == product.id } }
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
                storagesWithStocks.forEach { storage ->
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
                productsWithStocks.forEach { product ->
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

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredStocks) { stock ->
                StockItem(stock = stock)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun StockItem(stock: Stock) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Almacén: ${stock.storage.name}",
                style = MaterialTheme.typography.bodyLarge
            )
            stock.stock.forEach { (product, quantity) ->
                Text(
                    text = "Producto: ${product.name}, Cantidad: $quantity",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
