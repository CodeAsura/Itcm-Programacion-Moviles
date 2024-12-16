package com.example.walmart.screens

import android.icu.lang.UCharacter.toLowerCase
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.walmart.api.model.BusinessEntity
import com.example.walmart.api.model.Product
import com.example.walmart.api.model.Storage
import com.example.walmart.api.model.TransactionPost
import com.example.walmart.api.model.originDestiny
import com.example.walmart.components.DropdownMenuField
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun AddTransactionScreen(
    products: List<Product>,
    storages: List<Storage>,
    providers: List<BusinessEntity>,
    clients: List<BusinessEntity>,
    employee: BusinessEntity?,
    onAddTransacction: (TransactionPost) -> Unit,
    onCancel: () -> Unit
) {
    var scannedData by remember { mutableStateOf("") }
    var product by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf(TextFieldValue("")) }
    var destinationType by remember { mutableStateOf("") }
    var destinationName by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var typeOriginDestiny by remember { mutableStateOf("") }

    var selectedStorage by remember { mutableStateOf<Storage?>(null) }
    var selectedProvider by remember { mutableStateOf<BusinessEntity?>(null) }
    var selectedClient by remember { mutableStateOf<BusinessEntity?>(null) }

    val flag by remember {
        derivedStateOf {
            product != null
                    && quantity.text.isNotBlank()
                    && (selectedStorage != null
                    || selectedProvider != null
                    || selectedClient != null)
        }
    }

    val context = LocalContext.current

    val qrLauncher =
        rememberLauncherForActivityResult(
            contract = ScanContract()
        ) { result ->
            val intent = result.contents
            val qrContent = intent ?: ""
            if (qrContent.isNotBlank()) {
                scannedData = qrContent
                val parts = qrContent.split("|")
                if (parts.size == 4) {
                    val upc = parts[0]
                    product = products.find { it.upc == upc }
                    quantity = TextFieldValue(parts[1])
                    destinationType = parts[2]
                    destinationName = parts[3]

                    //Para esta parte se use que si el QR incluye un movimiento con Proveedor o
                    //Cliente usará el rfc en lugar del nombre, por cualquier motivo se tiene
                    //opción para ambos
                    if (destinationType.contains("Adquisición")) {
                        typeOriginDestiny = "PROVIDER"
                        selectedProvider =
                            providers.find { it.rfc == destinationName || it.name == destinationName }
                    } else if (destinationType.contains("Venta")) {
                        typeOriginDestiny = "CLIENT"
                        selectedClient =
                            clients.find { it.rfc == destinationName || it.name == destinationName }
                    } else {
                        typeOriginDestiny = "STORAGE"
                        selectedStorage = storages.find { it.name == destinationName }
                    }

                    type = if (parts[2].contains("Entrada", ignoreCase = true)) "IN" else "OUT"

                    if (selectedStorage != null
                        || selectedProvider != null
                        || selectedClient != null){
                        Toast.makeText(
                            context, "Datos leidos correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }else{
                        Toast.makeText(
                            context, "Error en el tipo de transacción",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context, "Error en el tamaño de los datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context, "El QR está vacío",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(ScrollState(0))
    ) {
        Text(
            "Añadir Transacción",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedButton(
            onClick = {
                qrLauncher.launch(ScanOptions())

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Escanear QR")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Datos del QR")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = product?.name ?: "",
            onValueChange = { },
            enabled = false,
            label = { Text("Producto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            enabled = false,
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = destinationType,
            onValueChange = { },
            enabled = false,
            label = { Text("Tipo de Transacción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (typeOriginDestiny) {
            "CLIENT" -> {
                DropdownMenuField(
                    label = "Cliente",
                    items = clients,
                    selectedItem = selectedClient,
                    onItemSelected = { selectedClient = it },
                    itemToString = { it.name }
                )
            }

            "PROVIDER" -> {
                DropdownMenuField(
                    label = "Proveedor",
                    items = providers,
                    selectedItem = selectedProvider,
                    onItemSelected = { selectedProvider = it },
                    itemToString = { it.name }
                )
            }

            "STORAGE" -> {
                DropdownMenuField(
                    label = "Almacén",
                    items = storages,
                    selectedItem = selectedStorage,
                    onItemSelected = { selectedStorage = it },
                    itemToString = { it.name }
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Text("Transaction Type: $type")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                var originDestiny = 1

                when (typeOriginDestiny) {
                    "STORAGE" -> {
                        originDestiny = selectedStorage?.id ?: 1
                        selectedStorage = null
                    }

                    "PROVIDER" -> {
                        originDestiny = selectedProvider?.id ?: 1
                        selectedProvider = null
                    }

                    "CLIENT" -> {
                        originDestiny = selectedClient?.id ?: 1
                        selectedClient = null
                    }
                }

                onAddTransacction(
                    TransactionPost(
                        storageId = 1,
                        productId = product?.id ?: 1,
                        employeeId = employee?.id ?: 1,
                        originDestiny = originDestiny(
                            id = originDestiny,
                            type = typeOriginDestiny
                        ),
                        quantity = quantity.text.toInt(),
                        type = type,
                    )
                )
            },
            enabled = flag,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir transacción")
        }

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

