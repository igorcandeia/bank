package com.challenge.model.merchant

import kotlinx.serialization.Serializable

@Serializable
data class Merchant(
    val name: String,
    val mcc: String
)
