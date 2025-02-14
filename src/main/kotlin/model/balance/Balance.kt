package com.challenge.model.balance

import kotlinx.serialization.Serializable

@Serializable
data class Balance(
    val id: Int,
    val balanceId: String,
    val availableAmount: Long,
    val accountId: String
)
