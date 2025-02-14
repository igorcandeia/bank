package model.balance

import com.challenge.DatabaseTest
import com.challenge.db.BalanceDAO
import com.challenge.model.balance.Balance
import com.challenge.model.balance.BalanceRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class BalanceRepositoryImplTest : DatabaseTest() {

    private val repository = BalanceRepositoryImpl()

    @Test
    fun allBalances() = runTest {
        assertEquals(0, repository.allBalances().size)
        insertBalance("FOOD", 2000, "test")
        assertEquals(1, repository.allBalances().size)
    }

    @Test
    fun balancesByAccountId() = runTest {
        assertEquals(0, repository.balancesByAccountId("test2").size)
        insertBalance("FOOD", 1500, "test2")
        assertEquals(1, repository.balancesByAccountId("test2").size)
        val balanceFound = repository.balancesByAccountId("test2").first()
        assertEquals(balanceFound.balanceId, "FOOD")
        assertEquals(balanceFound.availableAmount, 1500)
    }

    @Test
    fun updateAmountsByIds() = runTest {
        val balances = repository.allBalances()
        val affectedAmounts = balances.map { Pair(it.id, 5000L) }
        repository.updateAmountsByIds(affectedAmounts)

        repository.allBalances().forEach {
            assertEquals(it.availableAmount, 5000L)
        }
    }

    @Test
    fun updateBalanceAmount() = runTest {
        insertBalance("MEAL", 1000, "test")

        val balance = repository.allBalances().first()
        assertEquals(balance.availableAmount, 1000L)
        assertEquals(balance.balanceId, "MEAL")
        assertEquals(balance.accountId, "test")
    }

    @Test
    fun addBalance() = runTest {
        assert(repository.allBalances().isEmpty())
        repository.addBalance(Balance(1, "balanceTest", 3000, "accountTest"))
        assert(repository.allBalances().size == 1)
        val balance = repository.allBalances().first()
        assertEquals(balance.balanceId, "balanceTest")
        assertEquals(balance.availableAmount, 3000L)
        assertEquals(balance.accountId, "accountTest")
    }

    private fun insertBalance(balance: String, amount: Long, account: String) {
        transaction {
            BalanceDAO.new {
                balanceId = balance
                availableAmount = amount
                accountId = account
            }
        }
    }
}