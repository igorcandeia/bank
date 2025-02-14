package com.challenge.model.account


interface AccountRepository {
    suspend fun allAccounts(): List<Account>
    suspend fun addAccount(account: Account)
}