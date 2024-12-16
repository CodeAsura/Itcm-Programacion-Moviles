package com.example.walmart.api

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.walmart.api.model.BusinessEntity
import com.example.walmart.api.model.Product
import com.example.walmart.api.model.Stock
import com.example.walmart.api.model.Storage
import com.example.walmart.api.model.Transaction
import com.example.walmart.api.model.TransactionPost

class MainViewModel : ViewModel() {
    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products: State<List<Product>> = _products

    private val _transactions = mutableStateOf<List<Transaction>>(emptyList())
    val transactions: State<List<Transaction>> = _transactions

    private val _storages = mutableStateOf<List<Storage>>(emptyList())
    val storages = _storages

    private val _employees = mutableStateOf<List<BusinessEntity>>(emptyList())
    val employees = _employees

    private val _clients = mutableStateOf<List<BusinessEntity>>(emptyList())
    val clients = _clients

    private val _providers = mutableStateOf<List<BusinessEntity>>(emptyList())
    val providers = _providers

    private val _stock = mutableStateOf<List<Stock>>(emptyList())
    val stock = _stock

    var startDate = mutableStateOf("2024-11-28")
    var endDate = mutableStateOf("2024-12-31")

    private val _selectedStorage = mutableStateOf<Storage?>(null)
    val selectedStorage = _selectedStorage

    init {
        fetchAllData()
    }

    private fun <T> safeApiCall(apiCall: suspend () -> T, onSuccess: (T) -> Unit) {
        viewModelScope.launch {
            try {
                val result = apiCall()
                onSuccess(result)
            } catch (e: Exception) {
                Log.e("Error ViewModel", e.printStackTrace().toString())
            }
        }
    }

    private fun fetchAllData() {
        fetchProducts()
        fetchStorages()
        fetchTransactions()
        fetchEmployees()
        fetchClients()
        fetchProviders()
        fetchStock()
    }

    private fun fetchProducts() {
        safeApiCall(
            apiCall = { RetrofitInstance.productService.getProducts() },
            onSuccess = { _products.value = it }
        )
    }

    private fun fetchStorages() {
        safeApiCall(
            apiCall = { RetrofitInstance.storageService.getStorages() },
            onSuccess = { _storages.value = it }
        )
    }

    private fun fetchStock() {
        safeApiCall(
            apiCall = { RetrofitInstance.storageService.getStorageStock() },
            onSuccess = { _stock.value = it }
        )
    }

    fun fetchTransactions() {
        safeApiCall(
            apiCall = {
                val storage = _selectedStorage.value
                if (storage == null) {
                    val allTransactions = RetrofitInstance.transactionService.getAllTransactions(
                        startDate.value, endDate.value
                    )
                    allTransactions.flatMap { it.transactions }
                } else {
                    RetrofitInstance.transactionService.getTransactionsByStorage(
                        storage.id, startDate.value, endDate.value
                    )
                }
            },
            onSuccess = { _transactions.value = it }
        )
    }

    private fun fetchEmployees() {
        safeApiCall(
            apiCall = { RetrofitInstance.businessEntityService.getAllEmployees() },
            onSuccess = { _employees.value = it }
        )
    }

    private fun fetchClients() {
        safeApiCall(
            apiCall = { RetrofitInstance.businessEntityService.getAllClients() },
            onSuccess = { _clients.value = it }
        )
    }

    private fun fetchProviders() {
        safeApiCall(
            apiCall = { RetrofitInstance.businessEntityService.getAllProviders() },
            onSuccess = { _providers.value = it }
        )
    }

    fun onStorageSelected(storage: Storage?) {
        _selectedStorage.value = storage
        fetchTransactions()
    }

    fun addTransaction(transaction: TransactionPost) {
        safeApiCall(
            apiCall = { RetrofitInstance.transactionService.addTransaction(transaction) },
            onSuccess = {
                fetchTransactions()
                fetchStock()
            }
        )
    }
}
