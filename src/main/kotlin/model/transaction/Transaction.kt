package com.challenge.model.transaction

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val account: String,
    val amount: Double,
    val merchant: String,
    val mcc: String
)