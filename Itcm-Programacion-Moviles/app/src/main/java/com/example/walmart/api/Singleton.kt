package com.example.walmart.api

import com.example.walmart.api.service.BusinessEntityService
import com.example.walmart.api.service.ProductService
import com.example.walmart.api.service.StorageService
import com.example.walmart.api.service.TransactionService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.0.7:8080"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productService: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }

    val transactionService: TransactionService by lazy {
        retrofit.create(TransactionService::class.java)
    }

    val storageService: StorageService by lazy {
        retrofit.create(StorageService::class.java)
    }

    val businessEntityService: BusinessEntityService by lazy {
        retrofit.create(BusinessEntityService::class.java)
    }
}