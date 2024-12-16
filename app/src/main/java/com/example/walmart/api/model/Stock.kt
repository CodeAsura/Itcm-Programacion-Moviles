package com.example.walmart.api.model

data class Stock(
    val storage: Storage,
    val stock: List<StockStock>
)

data class StockStock(
    val product: Product,
    val quantity: Int
)