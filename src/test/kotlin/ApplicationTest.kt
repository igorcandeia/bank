package com.challenge

import com.challenge.model.account.Account
import com.challenge.model.account.AccountRepositoryImpl
import com.challenge.model.balance.Balance
import com.challenge.model.balance.BalanceRepositoryImpl
import com.challenge.model.merchant.Merchant
import com.challenge.model.merchant.MerchantRepositoryImpl
import com.challenge.model.transaction.TransactionRepositoryImpl
import com.challenge.plugins.TransactionResponse
import com.challenge.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.*

class ApplicationTest : DatabaseTest() {

    @Test
    fun testEndpoints() = testApplication {
        application {
            val transactionRepository = TransactionRepositoryImpl()
            val accountRepository = AccountRepositoryImpl()
            val balanceRepository = BalanceRepositoryImpl()
            val merchantRepository = MerchantRepositoryImpl()

            configureSerialization(transactionRepository, accountRepository, balanceRepository, merchantRepository)
        }

        val jsonSerializer = Json {
            ignoreUnknownKeys = true
            isLenient = false
        }

        runBlocking {
            val accountsResponse = client.get("/accounts")
            val accountsResponseBody: List<Account> = jsonSerializer.decodeFromString(accountsResponse.bodyAsText())

            assert(accountsResponseBody.isEmpty())
        }

        //TEST Add Account
        runBlocking {
            val accountJsonString = """
            {
                "accountId": "123"
            }
        """.trimIndent()

            client.post("/accounts") {
                contentType(ContentType.Application.Json)
                setBody(accountJsonString)
            }
            val addedAccounts = client.get("/accounts")
            val addedAccountsResponse: List<Account> = jsonSerializer.decodeFromString(addedAccounts.bodyAsText())
            assert(addedAccountsResponse.size == 1)
            val addedAccount = addedAccountsResponse.first()
            assertEquals("123", addedAccount.accountId)
        }

        //TEST Get Account Balances
        runBlocking {
            val accountBalances = client.get("/balances/account/123")
            val accountBalancesResponse: List<Balance> = jsonSerializer.decodeFromString(accountBalances.bodyAsText())
            assertEquals(3, accountBalancesResponse.size)
            //Ensure all balances has 0 availableAmount
            accountBalancesResponse.forEach { balance -> assertEquals(0, balance.availableAmount) }
        }

        //TEST Update Account Balance
        runBlocking {
            client.put("/balances/account/123/balance/MEAL/amount/5000")
            //Ensure account balance MEAL has 5000 (50.00) on availableBalance
            val balancesToCheck = client.get("/balances/account/123")
            val balancesToCheckResponse: List<Balance> = jsonSerializer.decodeFromString(balancesToCheck.bodyAsText())
            val mealBalance = balancesToCheckResponse.find { it.balanceId == "MEAL" }
            assertNotNull(mealBalance)
            assertEquals(5000, mealBalance.availableAmount)
        }

        //TEST Transaction
        runBlocking {
            val transactionJsonString = """
            {
                "account": "123",
                "amount": 50.00,
                "mcc": "5811",
                "merchant": "PADARIA DO ZE               SAO PAULO BR"
            }
            """.trimIndent()
            val transactionResponse = client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody(transactionJsonString)
            }
            val transactionsResponseBody: TransactionResponse =
                jsonSerializer.decodeFromString(transactionResponse.bodyAsText())
            assertEquals("00", transactionsResponseBody.code)
        }


        //TEST Transaction insufficient funds
        runBlocking {
            val transactionJsonString = """
            {
                "account": "123",
                "amount": 50.00,
                "mcc": "5811",
                "merchant": "PADARIA DO ZE               SAO PAULO BR"
            }
            """.trimIndent()
            val transactionResponse = client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody(transactionJsonString)
            }
            val transactionsResponseBody: TransactionResponse =
                jsonSerializer.decodeFromString(transactionResponse.bodyAsText())
            assertEquals("51", transactionsResponseBody.code)
        }

        //TEST Transaction with fallback to CASH (using MEAL + CASH funds)
        runBlocking {
            client.put("/balances/account/123/balance/MEAL/amount/5000")
            client.put("/balances/account/123/balance/CASH/amount/5000")
            val transactionJsonString = """
            {
                "account": "123",
                "amount": 100.00,
                "mcc": "5811",
                "merchant": "PADARIA DO ZE               SAO PAULO BR"
            }
            """.trimIndent()
            val transactionResponse = client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody(transactionJsonString)
            }
            val transactionsResponseBody: TransactionResponse =
                jsonSerializer.decodeFromString(transactionResponse.bodyAsText())
            assertEquals("00", transactionsResponseBody.code)
            val accountBalances = client.get("/balances/account/123")
            val accountBalancesResponse: List<Balance> = jsonSerializer.decodeFromString(accountBalances.bodyAsText())
            assertEquals(3, accountBalancesResponse.size)
            //Ensure all balances has 0 availableAmount
            accountBalancesResponse.forEach { balance -> assertEquals(0, balance.availableAmount) }
        }

        //TEST Transaction overriding mcc by merchant
        runBlocking {
            val merchantJsonString = """
            {
                "name": "PADARIA DO ZE",
                "mcc": "5412"
            }
            """.trimIndent()
            client.post("/merchants") {
                contentType(ContentType.Application.Json)
                setBody(merchantJsonString)
            }
            //Ensure merchant was added
            val merchants = client.get("/merchants")
            val merchantResponse: List<Merchant> = jsonSerializer.decodeFromString(merchants.bodyAsText())
            assertEquals("PADARIA DO ZE", merchantResponse.first().name)
            assertEquals("5412", merchantResponse.first().mcc)
            //PUT Funds on FOOD Balance
            client.put("/balances/account/123/balance/FOOD/amount/10000")
            val transactionJsonString = """
            {
                "account": "123",
                "amount": 100.00,
                "mcc": "5811",
                "merchant": "PADARIA DO ZE               SAO PAULO BR"
            }
            """.trimIndent()
            val transactionResponse = client.post("/transactions") {
                contentType(ContentType.Application.Json)
                setBody(transactionJsonString)
            }
            val transactionsResponseBody: TransactionResponse =
                jsonSerializer.decodeFromString(transactionResponse.bodyAsText())
            assertEquals("00", transactionsResponseBody.code)
        }
    }
}
