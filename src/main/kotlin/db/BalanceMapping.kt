package com.challenge.db

import com.challenge.model.balance.Balance
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object BalanceTable : IntIdTable("balance") {
    val balanceId = varchar("balance_id", 50)
    val availableAmount = long("available_amount")
    val accountId = varchar("account_id", 50)

    init {
        uniqueIndex("balance_id", balanceId, accountId)
    }
}

class BalanceDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BalanceDAO>(BalanceTable)

    var balanceId by BalanceTable.balanceId
    var availableAmount by BalanceTable.availableAmount
    var accountId by BalanceTable.accountId
}

fun daoToModel(dao: BalanceDAO) = Balance(dao.id.value, dao.balanceId, dao.availableAmount, dao.accountId)