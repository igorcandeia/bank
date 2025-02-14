package com.challenge.model.balance

import com.challenge.model.transaction.Transaction

interface BalanceRepository {
    suspend fun allBalances(): List<Balance>
    suspend fun balancesByAccountId(accountId: String): List<Balance>
    suspend fun updateAmountsByIds(balanceIdAmountList: List<Pair<Int, Long>>)
    suspend fun updateBalanceAmount(accountId: String, balanceId: String, amount: Long)
    suspend fun addBalance(balance: Balance)
}