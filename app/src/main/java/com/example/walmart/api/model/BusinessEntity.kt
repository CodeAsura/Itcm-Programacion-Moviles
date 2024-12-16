package com.example.walmart.api.model

data class BusinessEntity(
    val id: Int,
    val name: String,
    val rfc: String,
    val email: String,
    val phone: String,
    val type: String,
    val created_on: String
)