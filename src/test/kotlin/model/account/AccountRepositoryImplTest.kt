package model.account

import com.challenge.DatabaseTest
import com.challenge.db.AccountDAO
import com.challenge.model.account.Account
import com.challenge.model.account.AccountRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class AccountRepositoryImplTest : DatabaseTest() {

    private val repository = AccountRepositoryImpl()

    @Test
    fun allAccounts() = runTest {
        assert(repository.allAccounts().isEmpty())

        val account = Account("test")
        repository.addAccount(account)
        assertFalse(repository.allAccounts().isEmpty())

        transaction { AccountDAO.new { accountId = "test2" } }
        assertEquals(repository.allAccounts().size, 2)

        assertTrue(repository.allAccounts().contains(account))
    }

    @Test
    fun addAccount() = runTest {
        val account = Account("testAddAccount")
        repository.addAccount(account)
        assertTrue(repository.allAccounts().contains(account))

        assertFails { repository.addAccount(account) }
    }
}