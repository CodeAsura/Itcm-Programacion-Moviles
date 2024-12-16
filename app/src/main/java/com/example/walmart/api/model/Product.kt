package com.example.walmart.api.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val lastCost: String,
    val lastPrice: String,
    val upc: String
)
