import com.example.bleibboard.data.local.Tests
import com.example.bleibboard.data.local.TestsDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AthleteTestsRepository @Inject constructor(private val testsDao: TestsDao) {

    suspend fun upsertTest(test: Tests) {
        testsDao.upsertTest(test)
    }

    suspend fun deleteTest(test: Tests) {
        testsDao.deleteTest(test)
    }

    fun queryTestsOrderedByFirstName(): Flow<List<Tests>> {
        return testsDao.queryTestsOrderedByFirstName()
    }

    fun queryTestsOrderedByLastName(): Flow<List<Tests>> {
        return testsDao.queryTestsOrderedByLastName()
    }

    fun queryTestsOrderedByDate(): Flow<List<Tests>> {
        return testsDao.queryTestsOrderedByDate()
    }
}