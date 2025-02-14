package com.challenge.db

import com.challenge.model.account.Account
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object AccountTable : IntIdTable("account") {
    val accountId = varchar("account_id", 50).uniqueIndex()
}

class AccountDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccountDAO>(AccountTable)

    var accountId by AccountTable.accountId
}

fun daoToModel(dao: AccountDAO) = Account(dao.accountId)