package com.example.walmart

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.walmart.api.MainViewModel
import com.example.walmart.api.model.BusinessEntity
import com.example.walmart.components.BottomNavBar
import com.example.walmart.screens.AddTransactionScreen
import com.example.walmart.screens.LoginScreen
import com.example.walmart.screens.ProductClientScreen
import com.example.walmart.screens.ProductDetailScreen
import com.example.walmart.screens.ProductScreen
import com.example.walmart.screens.StockScreen
import com.example.walmart.screens.TransactionScreen
import com.example.walmart.ui.theme.WalmartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalmartTheme {
                MainScreen()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    var user by remember { mutableStateOf<BusinessEntity?>(null) }
    var userType by remember { mutableStateOf(UserType.NONE) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val navController = rememberNavController()
    val context = LocalContext.current
    val currentScreen = navController.currentBackStackEntryAsState()
    val isLoginScreen = currentScreen.value?.destination?.route == Screens.LOGIN.name

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                    Row(
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_walmart),
                            contentDescription = "Logo"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Wal-Mart",
                            modifier = Modifier.align(Alignment.CenterVertically))
                    }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF041f41),
                titleContentColor = Color(0xFFF0F0F0),
            ),
            actions = {
                if (!isLoginScreen) {
                    IconButton(onClick = {
                        showLogoutDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar Sesión",
                            tint = Color(0xFFF0F0F0)
                        )
                    }
                }
            },
        )
    }, bottomBar = {
        if (userType == UserType.EMPLOYEE) {
            BottomNavBar(navController = navController)
        }
    }) { innerPadding ->

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Cerrar Sesión") },
                text = { Text(text = "¿Estás seguro de que deseas cerrar sesión?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            user = null
                            userType = UserType.NONE
                            showLogoutDialog = false
                            navController.navigate(Screens.LOGIN.name) {
                                popUpTo(Screens.LOGIN.name) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                        }
                    ) {
                        Text("No")
                    }
                }
            )
        }

        NavHost(
            navController = navController,
            startDestination = Screens.LOGIN.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.LOGIN.name) {
                LoginScreen(onLoginAsCustomer = { email, password ->
                    val users = viewModel.clients.value

                    user = users.find { it.email == email && it.rfc == password }

                    if (user != null) {
                        userType = UserType.CLIENT
                        navController.navigate(Screens.CLIENT.name) {
                            popUpTo(Screens.LOGIN.name) {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(context, "Datos inválidos", Toast.LENGTH_SHORT).show()
                        /*navController.navigate(Screens.CLIENT.name) {
                            popUpTo(Screens.LOGIN.name) {
                                inclusive = true
                            }
                        } // TODO: Borrar esta madre*/
                    }
                }, onLoginAsEmployee = { email, password ->
                    val users = viewModel.employees.value

                    val user = users.find { it.email == email && it.rfc == password }

                    if (user != null || (email == "admin" && password == "admin")) {
                        userType = UserType.EMPLOYEE
                        navController.navigate(Screens.PRODUCTS.name) {
                            popUpTo(Screens.LOGIN.name) {
                                inclusive = true
                            }
                        }
                    } else {
                        Toast.makeText(context, "Datos inválidos", Toast.LENGTH_SHORT).show()
                        userType = UserType.EMPLOYEE
                        navController.navigate(Screens.PRODUCTS.name) {
                            popUpTo(Screens.LOGIN.name) {
                                inclusive = true
                            }
                        }
                    }
                })
            }

            composable(Screens.CLIENT.name) {
                ProductClientScreen(
                    products = viewModel.products.value,
                    onClick = { product ->
                        navController.navigate(Screens.BUY_PRODUCT.name + "/${product.id}")
                    })
            }

            composable(Screens.PRODUCTS.name) {
                ProductScreen(products = viewModel.products.value)
            }

            composable(Screens.BUY_PRODUCT.name + "/{productId}") { backStackEntry ->
                val productId =
                    backStackEntry.arguments?.getString("productId")?.toInt() ?: return@composable
                ProductDetailScreen(
                    product = viewModel.products.value.find { it.id == productId },
                    client = user!!,
                    onAddTransaction = { transaction ->
                        viewModel.addTransaction(transaction)
                    }
                )
            }

            composable(Screens.TRANSACTIONS.name) {
                if (userType == UserType.EMPLOYEE) {
                    TransactionScreen(transactions = viewModel.transactions.value,
                        storages = viewModel.storages.value,
                        products = viewModel.products.value,
                        startDate = viewModel.startDate.value,
                        endDate = viewModel.endDate.value,
                        onDateChange = { start, end ->
                            viewModel.startDate.value = start
                            viewModel.endDate.value = end
                            viewModel.fetchTransactions()
                        },
                        onChangeToAddTransaction = {
                            navController.navigate(Screens.ADD_TRANSACTION.name)
                        }
                    )
                } else {
                    navController.navigate(Screens.CLIENT.name)
                }
            }

            composable(Screens.ADD_TRANSACTION.name) {
                AddTransactionScreen(
                    products = viewModel.products.value,
                    storages = viewModel.storages.value,
                    employee = user,
                    providers = viewModel.providers.value,
                    clients = viewModel.clients.value,
                    onAddTransacction = { transaction ->
                        viewModel.addTransaction(transaction)
                        Toast.makeText(
                            context, "Transacción agregada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(Screens.TRANSACTIONS.name)
                    },
                    onCancel = {
                        navController.navigate(Screens.TRANSACTIONS.name)
                    }
                )
            }

            composable(Screens.STOCK.name) {
                StockScreen(
                    products = viewModel.products.value,
                    storages = viewModel.storages.value,
                    stocks = viewModel.stock.value
                )
            }
        }
    }
}
