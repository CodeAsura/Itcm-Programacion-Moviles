package com.example.walmart.api.service

import com.example.walmart.api.model.Product
import retrofit2.http.GET

interface ProductService {
    @GET("/api/v1/product")
    suspend fun getProducts(): List<Product>
}
