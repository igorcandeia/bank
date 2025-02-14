package com.challenge

import com.challenge.db.AccountTable
import com.challenge.db.BalanceTable
import com.challenge.db.MerchantTable
import com.challenge.db.TransactionTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

object TestDatabase {
    private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:13.3").apply {
        withDatabaseName("testdb")
        withUsername("testuser")
        withPassword("testpass")
        start()
    }

    init {
        val config = HikariConfig().apply {
            jdbcUrl = postgresContainer.jdbcUrl
            username = postgresContainer.username
            password = postgresContainer.password
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(AccountTable)
            SchemaUtils.create(BalanceTable)
            SchemaUtils.create(TransactionTable)
            SchemaUtils.create(MerchantTable)
        }
    }
}

fun clearDatabase() {
    transaction {
        AccountTable.deleteAll()
        BalanceTable.deleteAll()
        TransactionTable.deleteAll()
        MerchantTable.deleteAll()
    }
}

abstract class DatabaseTest {
    @BeforeTest
    fun setUp() {
        TestDatabase
    }

    @AfterTest
    fun tearDown() {
        clearDatabase()
    }
}