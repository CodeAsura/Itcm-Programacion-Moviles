package com.example.walmart.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.walmart.api.model.BusinessEntity
import com.example.walmart.api.model.Product
import com.example.walmart.api.model.TransactionPost
import com.example.walmart.api.model.originDestiny
import com.example.walmart.moneyFormat

@Composable
fun ProductDetailScreen(
    product: Product?,
    client: BusinessEntity,
    onAddTransaction: (TransactionPost) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    if (product != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Selecciona la cantidad", style = MaterialTheme.typography.titleLarge)

            Text(text = "Producto: ${product.name}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Precio: $${product.lastPrice.toDouble().moneyFormat()}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Decrementar cantidad"
                    )
                }
                Text(text = "$quantity", style = MaterialTheme.typography.bodySmall)
                IconButton(onClick = { quantity++ }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Incrementar cantidad"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onAddTransaction(
                        TransactionPost(
                            storageId = 1,
                            productId = product.id,
                            employeeId = 1,
                            quantity = quantity,
                            originDestiny = originDestiny(
                                id = client.id,
                                type = "CLIENT"
                            ),
                            type = "OUT"
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Comprar")
            }
        }

    } else {
        Text(text = "Producto no encontrado", style = MaterialTheme.typography.bodyLarge)
    }
}
