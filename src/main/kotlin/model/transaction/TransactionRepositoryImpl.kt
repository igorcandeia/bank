package com.challenge.model.transaction

import com.challenge.db.TransactionDAO
import com.challenge.db.TransactionTable
import com.challenge.db.daoToModel
import com.challenge.db.utils.suspendTransaction

class TransactionRepositoryImpl : TransactionRepository {
    override suspend fun allTransactions(): List<Transaction> = suspendTransaction {
        TransactionDAO.all().map { daoToModel(it) }
    }

    override suspend fun transactionsByAccountId(accountId: String): List<Transaction> = suspendTransaction {
        TransactionDAO
            .find { TransactionTable.accountId eq accountId }
            .map { daoToModel(it) }
    }

    override suspend fun addTransaction(transaction: Transaction): Unit = suspendTransaction {
        val amountCents = (transaction.amount * 100).toLong()

        TransactionDAO.new {
            accountId = transaction.account
            amount = amountCents
            merchant = transaction.merchant
            mcc = transaction.mcc
        }
    }
}