package com.challenge.model.merchant

import com.challenge.db.MerchantDAO
import com.challenge.db.daoToModel
import com.challenge.db.utils.suspendTransaction

class MerchantRepositoryImpl : MerchantRepository {
    override suspend fun allMerchants(): List<Merchant> = suspendTransaction {
        MerchantDAO.all().map { daoToModel(it) }
    }

    override suspend fun addMerchant(merchant: Merchant): Unit = suspendTransaction {
        MerchantDAO.new {
            name = merchant.name
            mcc = merchant.mcc
        }
    }
}