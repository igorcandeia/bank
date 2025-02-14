package com.challenge.model.transaction

interface TransactionRepository {
    suspend fun allTransactions(): List<Transaction>
    suspend fun transactionsByAccountId(accountId: String): List<Transaction>
    suspend fun addTransaction(transaction: Transaction)
}