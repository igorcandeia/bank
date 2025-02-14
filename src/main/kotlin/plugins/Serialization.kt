package com.challenge.plugins

import com.challenge.model.account.Account
import com.challenge.model.account.AccountRepository
import com.challenge.model.balance.BalanceRepository
import com.challenge.model.merchant.Merchant
import com.challenge.model.merchant.MerchantRepository
import com.challenge.model.transaction.Transaction
import com.challenge.model.transaction.TransactionRepository
import com.challenge.validators.validateAvailableBalances
import com.challenge.validators.validateMerchantAndOverrideMCC
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.exposedLogger

const val APPROVED_CODE = "00"
const val INSUFFICIENT_FUNDS_CODE = "51"
const val FAILED_CODE = "07"

@Serializable
data class TransactionResponse(val code: String)

fun Application.configureSerialization(
    transactionRepository: TransactionRepository,
    accountRepository: AccountRepository,
    balanceRepository: BalanceRepository,
    merchantRepository: MerchantRepository
) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        route("/transactions") {
            get {
                val transactions = transactionRepository.allTransactions()
                call.respond(transactions)
            }

            get("/account/{accountId}") {
                val accountId = call.parameters["accountId"]
                if (accountId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val transactions = transactionRepository.transactionsByAccountId(accountId)
                call.respond(transactions)
            }

            post {
                try {
                    val receivedTransaction = call.receive<Transaction>()
                    val merchants = merchantRepository.allMerchants()
                    val transaction = validateMerchantAndOverrideMCC(receivedTransaction, merchants)

                    val balances = balanceRepository.balancesByAccountId(transaction.account)
                    if (balances.isEmpty()) {
                        exposedLogger.error("No balances found for account ${transaction.account}")
                        call.respond(TransactionResponse(FAILED_CODE))
                        return@post
                    }

                    val affectedBalances = validateAvailableBalances(balances, transaction)
                    if (affectedBalances.isEmpty()) {
                        exposedLogger.error("Insufficient funds for account ${transaction.account}")
                        call.respond(TransactionResponse(INSUFFICIENT_FUNDS_CODE))
                        return@post
                    }

                    val balanceIdAmountList = affectedBalances.map { (balanceId, newAmount) ->
                        val id = balances.find { it.balanceId == balanceId }?.id
                        if (id == null) {
                            call.respond(TransactionResponse(FAILED_CODE))
                            return@post
                        }
                        Pair(id, newAmount)
                    }
                    balanceRepository.updateAmountsByIds(balanceIdAmountList)

                    transactionRepository.addTransaction(transaction)
                    exposedLogger.info("Transaction success for account ${transaction.account}")
                    call.respond(TransactionResponse(APPROVED_CODE))
                } catch (e: Exception) {
                    exposedLogger.error("Transaction error, message: ${e.message}")
                    call.respond(TransactionResponse(FAILED_CODE))
                }
            }
        }

        route("/accounts") {
            get {
                val accounts = accountRepository.allAccounts()
                call.respond(accounts)
            }

            post {
                try {
                    val account = call.receive<Account>()
                    accountRepository.addAccount(account)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

        route("/balances") {
            get("/account/{accountId}") {
                val accountId = call.parameters["accountId"]
                if (accountId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val balances = balanceRepository.balancesByAccountId(accountId)
                call.respond(balances)
            }

            put("/account/{accountId}/balance/{balanceId}/amount/{amount}") {
                try {
                    val accountId = call.parameters["accountId"]
                    val balanceId = call.parameters["balanceId"]
                    val amount = call.parameters["amount"]
                    exposedLogger.info("Update balance for: Account $accountId balance $balanceId amount $amount")

                    if (balanceId == null || accountId == null || amount == null || amount.toLong() < 0) {
                        call.respond(HttpStatusCode.BadRequest)
                    } else {
                        balanceRepository.updateBalanceAmount(accountId, balanceId, amount.toLong())
                        call.respond(HttpStatusCode.OK)
                    }
                } catch (e: Exception) {
                    exposedLogger.error("Error to update account balance, message: ${e.message}")
                    call.respond(HttpStatusCode.BadRequest)
                }

            }
        }

        route("/merchants") {
            get {
                val merchants = merchantRepository.allMerchants()
                call.respond(merchants)
            }

            post {
                try {
                    val merchant = call.receive<Merchant>()
                    merchantRepository.addMerchant(merchant)
                    call.respond(HttpStatusCode.NoContent)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
