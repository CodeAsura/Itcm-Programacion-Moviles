package com.example.walmart.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walmart.api.model.Product
import com.example.walmart.moneyFormat

@Composable
fun ProductItem(product: Product) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.titleLarge)
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "UPC: ${product.upc}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Costo: $${product.lastCost.toDouble().moneyFormat()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Precio: $${product.lastPrice.toDouble().moneyFormat()}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ProductItem(product: Product, onClick: (Product) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick(product) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = product.name, style = MaterialTheme.typography.titleLarge)
            Text(text = product.description, style = MaterialTheme.typography.bodyMedium)
            Text(text = "UPC: ${product.upc}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Precio: $${product.lastPrice.toDouble().moneyFormat()}", style = MaterialTheme.typography.bodySmall)
        }
    }
}