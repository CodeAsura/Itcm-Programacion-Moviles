package com.example.walmart.api.model

data class Transaction(
    val id: Int,
    val storage: Storage,
    val product: Product,
    val businessEntity: BusinessEntity,
    val originDestiny: Any,
    val quantity: Int,
    val type: String,
    val createdAt: String
)

data class originDestiny(
    val id: Int,
    val type: String
)

data class TransactionPost(
    val storageId: Int,
    val productId: Int,
    val employeeId: Int,
    val quantity: Int,
    val originDestiny: originDestiny,
    val type: String,
)

data class TransactionByStorage(
    val storage: Storage,
    val transactions: List<Transaction>
)