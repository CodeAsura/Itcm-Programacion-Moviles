package com.example.walmart.api.service

import com.example.walmart.api.model.Transaction
import com.example.walmart.api.model.TransactionByStorage
import com.example.walmart.api.model.TransactionPost
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionService {
    @GET("/api/v1/transaction")
    suspend fun getAllTransactions(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<TransactionByStorage>

    @GET("/api/v1/transaction/storage/{storageId}")
    suspend fun getTransactionsByStorage(
        @Path("storageId") storageId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<Transaction>

    @GET("/api/v1/transaction/storage/{storageId}/product/{productId}")
    suspend fun getTransactionsByStorageAndByProduct(
        @Path("storageId") storageId: Int,
        @Path("productId") productId: Int,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<Transaction>

    @POST("/api/v1/transaction")
    suspend fun addTransaction(@Body transaction: TransactionPost): Transaction

    @PUT("/api/v1/transaction")
    suspend fun updateTransaction(@Body transaction: Transaction): Transaction

    @DELETE("/api/v1/transaction")
    suspend fun deleteTransaction(@Query("transactionId") transactionId: Int)
}
