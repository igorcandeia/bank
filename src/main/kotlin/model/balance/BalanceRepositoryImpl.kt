package com.challenge.model.balance

import com.challenge.db.*
import com.challenge.db.utils.suspendTransaction
import com.challenge.model.transaction.Transaction
import com.challenge.plugins.FAILED_CODE
import com.challenge.plugins.INSUFFICIENT_FUNDS_CODE
import com.challenge.plugins.TransactionResponse
import com.challenge.validators.validateAvailableBalances
import io.ktor.server.response.*
import org.jetbrains.exposed.sql.Except
import org.jetbrains.exposed.sql.and

class BalanceRepositoryImpl : BalanceRepository {
    override suspend fun allBalances(): List<Balance> = suspendTransaction {
        BalanceDAO.all().map { daoToModel(it) }
    }

    override suspend fun balancesByAccountId(accountId: String): List<Balance> = suspendTransaction {
        BalanceDAO
            .find { BalanceTable.accountId eq accountId }
            .map { daoToModel(it) }
    }

    override suspend fun updateAmountsByIds(balanceIdAmountList: List<Pair<Int, Long>>): Unit = suspendTransaction {
        balanceIdAmountList.forEach { (id, amount) ->
            BalanceDAO.findByIdAndUpdate(id) { it.availableAmount = amount }
        }
    }

    override suspend fun updateBalanceAmount(accountId: String, balanceId: String, amount: Long): Unit =
        suspendTransaction {
            BalanceDAO
                .find { (BalanceTable.accountId eq accountId) and (BalanceTable.balanceId eq balanceId) }
                .first().let {
                    it.availableAmount = amount
                    it.flush()
                }
        }

    override suspend fun addBalance(balance: Balance): Unit = suspendTransaction {
        BalanceDAO.new {
            balanceId = balance.balanceId
            availableAmount = balance.availableAmount
            accountId = balance.accountId
        }
    }
}