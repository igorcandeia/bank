package model.transaction


import com.challenge.DatabaseTest
import com.challenge.db.TransactionDAO
import com.challenge.model.transaction.Transaction
import com.challenge.model.transaction.TransactionRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionRepositoryImplTest : DatabaseTest() {

    private val repository = TransactionRepositoryImpl()

    @Test
    fun allTransactions() = runTest {
        assertEquals(0, repository.allTransactions().size)
        insertTransaction("123", 2000, "MERCHANT TEST", "5811")
        assertEquals(1, repository.allTransactions().size)
    }

    @Test
    fun transactionsByAccountId() = runTest {
        assert(repository.transactionsByAccountId("123").isEmpty())
        insertTransaction("123", 2000, "MERCHANT TEST", "5811")
        assertEquals(1, repository.transactionsByAccountId("123").size)
        insertTransaction("123", 3000, "MERCHANT TEST", "5811")
        assertEquals(2, repository.transactionsByAccountId("123").size)
        insertTransaction("1234", 3000, "MERCHANT2 TEST", "5812")
        assertEquals(2, repository.transactionsByAccountId("123").size)
        assertEquals(1, repository.transactionsByAccountId("1234").size)
    }

    @Test
    fun addTransaction() = runTest {
        assertEquals(0, repository.allTransactions().size)
        val transaction = Transaction("123", 150.55, "PADARIA DO ZE               SAO PAULO BR", "5811")
        repository.addTransaction(transaction)
        val transactionSaved = repository.allTransactions().first()
        assertEquals(transaction.account, transactionSaved.account)
        assertEquals(transaction.amount, transactionSaved.amount)
        assertEquals(transaction.merchant, transactionSaved.merchant)
        assertEquals(transaction.mcc, transactionSaved.mcc)
    }

    private fun insertTransaction(account: String, totalAmount: Long, merchantInfo: String, mccCode: String) {
        transaction {
            TransactionDAO.new {
                accountId = account
                amount = totalAmount
                merchant = merchantInfo
                mcc = mccCode
            }
        }
    }
}