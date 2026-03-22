package meow.softer.mydiary.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import meow.softer.mydiary.data.local.db.dao.MemoDao
import meow.softer.mydiary.data.local.db.entity.MemoEntry

class MemoRepo @Inject constructor(private val memoDao: MemoDao) {
    suspend fun getAllByTopicId(topicId: Int): List<MemoEntry> {
        return withContext(Dispatchers.IO) {
            memoDao.getAllByTopicId(topicId)
        }
    }

    suspend fun updateMemo(memoEntry: MemoEntry) {
        withContext(Dispatchers.IO) {
            memoDao.update(memoEntry)
        }
    }

    suspend fun addMemo(memoEntry: MemoEntry) {
        withContext(Dispatchers.IO) {
            memoDao.add(memoEntry)
        }
    }

    suspend fun deleteMemo(memoEntry: MemoEntry) {
        withContext(Dispatchers.IO) {
            memoDao.delete(memoEntry)
        }
    }
}