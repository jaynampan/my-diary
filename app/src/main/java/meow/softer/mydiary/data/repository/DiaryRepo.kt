package meow.softer.mydiary.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meow.softer.mydiary.data.local.db.DiaryDatabase
import meow.softer.mydiary.data.local.db.dao.DiaryDao
import meow.softer.mydiary.data.local.db.dao.DiaryItemDao
import meow.softer.mydiary.data.local.db.entity.DiaryEntry
import meow.softer.mydiary.data.local.db.entity.DiaryItem
import javax.inject.Inject

class DiaryRepo @Inject constructor(
    private val diaryDatabase: DiaryDatabase,
    private val diaryDao: DiaryDao,
    private val diaryItemDao: DiaryItemDao
) {
    suspend fun getAllDiariesByTopicId(topicId: Int): List<DiaryEntry> {
        return withContext(Dispatchers.IO) {
            diaryDao.getAllByTopicId(topicId)
        }
    }

    suspend fun getDiaryById(id: Int): DiaryEntry? {
        return withContext(Dispatchers.IO) {
            diaryDao.getById(id)
        }
    }

    suspend fun getDiaryItems(diaryId: Int): List<DiaryItem> {
        return withContext(Dispatchers.IO) {
            diaryItemDao.getAllByDiaryId(diaryId)
        }
    }

    suspend fun saveDiary(diaryEntry: DiaryEntry, items: List<DiaryItem>) {
        withContext(Dispatchers.IO) {
            diaryDatabase.withTransaction {
                val diaryId = if (diaryEntry.id == 0) {
                    diaryDao.add(diaryEntry).toInt()
                } else {
                    diaryDao.update(diaryEntry)
                    diaryEntry.id
                }

                // Delete old items and insert new ones to maintain order and handle removals
                diaryItemDao.deleteByDiaryId(diaryId)
                items.forEachIndexed { index, item ->
                    diaryItemDao.add(item.copy(id = 0, refDiaryId = diaryId, position = index))
                }
            }
        }
    }

    suspend fun deleteDiary(diaryEntry: DiaryEntry) {
        withContext(Dispatchers.IO) {
            diaryDatabase.withTransaction {
                diaryItemDao.deleteByDiaryId(diaryEntry.id)
                diaryDao.delete(diaryEntry)
            }
        }
    }
}
