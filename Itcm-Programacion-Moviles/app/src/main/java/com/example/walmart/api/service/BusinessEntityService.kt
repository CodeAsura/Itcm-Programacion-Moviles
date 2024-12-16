package com.example.walmart.api.service

import com.example.walmart.api.model.BusinessEntity
import retrofit2.http.GET

interface BusinessEntityService {
    @GET("/api/v1/employee")
    suspend fun getAllEmployees(
    ): List<BusinessEntity>

    @GET("/api/v1/client")
    suspend fun getAllClients(
    ): List<BusinessEntity>

    @GET("/api/v1/provider")
    suspend fun getAllProviders(
    ): List<BusinessEntity>
}