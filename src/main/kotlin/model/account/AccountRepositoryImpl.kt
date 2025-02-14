package com.challenge.model.account

import com.challenge.db.AccountDAO
import com.challenge.db.BalanceDAO
import com.challenge.db.daoToModel
import com.challenge.db.utils.suspendTransaction

class AccountRepositoryImpl : AccountRepository {
    override suspend fun allAccounts(): List<Account> = suspendTransaction {
        AccountDAO.all().map { daoToModel(it) }
    }

    override suspend fun addAccount(account: Account): Unit = suspendTransaction {
        val defaultBalances = listOf("FOOD", "MEAL", "CASH")

        AccountDAO.new { accountId = account.accountId }

        defaultBalances.forEach { balanceId ->
            BalanceDAO.new {
                this.balanceId = balanceId
                this.availableAmount = 0
                this.accountId = account.accountId
            }
        }
    }
}
