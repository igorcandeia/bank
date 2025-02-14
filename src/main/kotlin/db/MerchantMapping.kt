package com.challenge.db

import com.challenge.model.merchant.Merchant
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MerchantTable : IntIdTable("merchant") {
    val name = varchar("name", 255)
    val mcc = varchar("mcc", 4)
}

class MerchantDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MerchantDAO>(MerchantTable)

    var name by MerchantTable.name
    var mcc by MerchantTable.mcc
}

fun daoToModel(dao: MerchantDAO) = Merchant(dao.name, dao.mcc)