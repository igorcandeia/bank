package model.merchant


import com.challenge.DatabaseTest
import com.challenge.db.MerchantDAO
import com.challenge.model.merchant.Merchant
import com.challenge.model.merchant.MerchantRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.Test
import kotlin.test.assertEquals

class MerchantRepositoryImplTest : DatabaseTest() {

    private val repository = MerchantRepositoryImpl()

    @Test
    fun allMerchants() = runTest {
        assertEquals(0, repository.allMerchants().size)
        insertMerchant("IFOOD", "5811")
        assertEquals(1, repository.allMerchants().size)
        val merchant = repository.allMerchants().first()
        assertEquals("IFOOD", merchant.name)
        assertEquals("5811", merchant.mcc)
        insertMerchant("UBER EATS", "5812")
        assertEquals(2, repository.allMerchants().size)
    }

    @Test
    fun addMerchant() = runTest {
        assertEquals(0, repository.allMerchants().size)
        val merchant = Merchant("UBER TRIP", "1234")
        repository.addMerchant(merchant)
        assertEquals(1, repository.allMerchants().size)
        val saved = repository.allMerchants().first()
        assertEquals(merchant.name, saved.name)
        assertEquals(merchant.mcc, saved.mcc)
    }

    private fun insertMerchant(merchantName: String, merchantMcc: String) {
        transaction {
            MerchantDAO.new {
                name = merchantName
                mcc = merchantMcc
            }
        }
    }
}