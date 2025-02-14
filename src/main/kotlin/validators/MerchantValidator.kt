package com.challenge.validators

import com.challenge.model.merchant.Merchant
import com.challenge.model.transaction.Transaction

fun validateMerchantAndOverrideMCC(transaction: Transaction, merchants: List<Merchant>): Transaction {
    val merchant = merchants.find { transaction.merchant.contains(it.name, ignoreCase = true) }
    println("Merchant ${merchant.toString()}")
    return if (merchant != null) {
        transaction.copy(
            mcc = merchant.mcc
        )
    } else {
        transaction
    }
}