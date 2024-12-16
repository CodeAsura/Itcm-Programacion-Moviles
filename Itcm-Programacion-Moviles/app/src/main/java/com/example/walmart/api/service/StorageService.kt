package com.example.walmart.api.service

import com.example.walmart.api.model.Stock
import com.example.walmart.api.model.Storage
import retrofit2.http.GET

interface StorageService {
    @GET("/api/v1/storage")
    suspend fun getStorages(): List<Storage>

    @GET("/api/v1/storage/stock")
    suspend fun getStorageStock(): List<Stock>
}