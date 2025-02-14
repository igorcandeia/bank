package com.challenge.validators

import com.challenge.model.balance.Balance
import com.challenge.model.transaction.Transaction

val FOOD_MCCS = listOf("5411", "5412")
val MEAL_MCCS = listOf("5811", "5812")
const val FOOD_BALANCE_ID = "FOOD"
const val MEAL_BALANCE_ID = "MEAL"
const val CASH_BALANCE_ID = "CASH"

fun validateAvailableBalances(
    balances: List<Balance>,
    transaction: Transaction
): List<Pair<String, Long>> {
    val (_, amount, _, mcc) = transaction
    val amountCents = (amount * 100).toLong()

    return when (mcc) {
        in FOOD_MCCS -> validateFoodBalance(balances, amountCents)
        in MEAL_MCCS -> validateMealBalance(balances, amountCents)
        else -> validateCashBalance(balances, amountCents)
    }
}


private fun validateFoodBalance(
    balances: List<Balance>,
    transactionAmount: Long
): List<Pair<String, Long>> {
    val foodAvailableAmount = balances.find { it.balanceId == FOOD_BALANCE_ID }?.availableAmount ?: 0
    val cashAvailableAmount = balances.find { it.balanceId == CASH_BALANCE_ID }?.availableAmount ?: 0

    return if (foodAvailableAmount >= transactionAmount) {
        val newBalanceAmount = foodAvailableAmount - transactionAmount
        listOf(Pair(FOOD_BALANCE_ID, newBalanceAmount))
    } else if (foodAvailableAmount + cashAvailableAmount >= transactionAmount) {
        val newCashBalanceAmount = cashAvailableAmount - (transactionAmount - foodAvailableAmount)
        listOf(Pair(FOOD_BALANCE_ID, 0), Pair(CASH_BALANCE_ID, newCashBalanceAmount))
    } else {
        emptyList()
    }
}

private fun validateMealBalance(
    balances: List<Balance>,
    transactionAmount: Long
): List<Pair<String, Long>> {
    val mealAvailableAmount = balances.find { it.balanceId == MEAL_BALANCE_ID }?.availableAmount ?: 0
    val cashAvailableAmount = balances.find { it.balanceId == CASH_BALANCE_ID }?.availableAmount ?: 0

    return if (mealAvailableAmount >= transactionAmount) {
        val newBalanceAmount = mealAvailableAmount - transactionAmount
        listOf(Pair(MEAL_BALANCE_ID, newBalanceAmount))
    } else if (mealAvailableAmount + cashAvailableAmount >= transactionAmount) {
        val newCashBalanceAmount = cashAvailableAmount - (transactionAmount - mealAvailableAmount)
        listOf(Pair(MEAL_BALANCE_ID, 0), Pair(CASH_BALANCE_ID, newCashBalanceAmount))
    } else {
        emptyList()
    }
}

private fun validateCashBalance(
    balances: List<Balance>,
    transactionAmount: Long
): List<Pair<String, Long>> {
    val cashAvailableAmount = balances.find { it.balanceId == CASH_BALANCE_ID }?.availableAmount ?: 0

    return if (cashAvailableAmount >= transactionAmount) {
        val newBalanceAmount = cashAvailableAmount - transactionAmount
        listOf(Pair(CASH_BALANCE_ID, newBalanceAmount))
    } else {
        emptyList()
    }
}