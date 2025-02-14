package com.challenge.model.merchant

interface MerchantRepository {
    suspend fun allMerchants(): List<Merchant>
    suspend fun addMerchant(merchant: Merchant)
}