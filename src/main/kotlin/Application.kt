package com.challenge

import com.challenge.model.account.AccountRepositoryImpl
import com.challenge.model.balance.BalanceRepositoryImpl
import com.challenge.model.merchant.MerchantRepositoryImpl
import com.challenge.model.transaction.TransactionRepositoryImpl
import com.challenge.plugins.configureDatabases
import com.challenge.plugins.configureRouting
import com.challenge.plugins.configureSerialization
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val transactionRepository = TransactionRepositoryImpl()
    val accountRepository = AccountRepositoryImpl()
    val balanceRepository = BalanceRepositoryImpl()
    val merchantRepository = MerchantRepositoryImpl()

    val config = environment.config
    val dbUrl = config.property("database.url").getString()
    val dbUser = config.property("database.user").getString()
    val dbPassword = config.property("database.password").getString()

    configureSerialization(transactionRepository, accountRepository, balanceRepository, merchantRepository)
    configureDatabases(dbUrl, dbUser, dbPassword)
    configureRouting()
}
