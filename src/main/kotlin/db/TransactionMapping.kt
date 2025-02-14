package com.challenge.db

import com.challenge.model.transaction.Transaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TransactionTable : IntIdTable("transaction") {
    val accountId = varchar("account_id", 50)
    val amount = long("amount")
    val merchant = varchar("merchant", 50)
    val mcc = varchar("mcc", 4)
}

class TransactionDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TransactionDAO>(TransactionTable)

    var accountId by TransactionTable.accountId
    var amount by TransactionTable.amount
    var merchant by TransactionTable.merchant
    var mcc by TransactionTable.mcc
}

fun daoToModel(dao: TransactionDAO) = Transaction(dao.accountId, (dao.amount.toDouble() / 100), dao.merchant, dao.mcc)