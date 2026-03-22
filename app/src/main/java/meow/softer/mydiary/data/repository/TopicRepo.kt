package meow.softer.mydiary.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meow.softer.mydiary.data.local.db.dao.ContactDao
import meow.softer.mydiary.data.local.db.dao.DiaryDao
import meow.softer.mydiary.data.local.db.dao.MemoDao
import meow.softer.mydiary.data.local.db.dao.TopicDao
import meow.softer.mydiary.data.local.db.dao.TopicOrderDao
import meow.softer.mydiary.data.local.db.entity.TopicEntry
import meow.softer.mydiary.data.local.db.entity.TopicOrder
import meow.softer.mydiary.ui.models.Contacts
import meow.softer.mydiary.ui.models.Diary
import meow.softer.mydiary.ui.models.ITopic
import meow.softer.mydiary.ui.models.Memo
import javax.inject.Inject

class TopicRepo @Inject constructor(
    private val topicDao: TopicDao,
    private val diaryDao: DiaryDao,
    private val memoDao: MemoDao,
    private val contactDao: ContactDao,
    private val topicOrderDao: TopicOrderDao
) {
    suspend fun getAll(): List<ITopic> {
        return withContext(Dispatchers.IO) {
            val topics = topicDao.getAllOrdered()
            // If no orders exist yet, we might get an empty list or unordered list depending on JOIN.
            val finalTopics = if (topics.isEmpty()) topicDao.getAll() else topics
            parseEntityToModel(finalTopics)
        }
    }

    suspend fun addTopic(topicEntry: TopicEntry) {
        withContext(Dispatchers.IO) {
            val id = topicDao.insert(topicEntry)
            val currentOrders = topicOrderDao.getAll()
            val nextOrder = (currentOrders.maxOfOrNull { it.order } ?: -1) + 1
            topicOrderDao.add(TopicOrder(refTopicId = id.toInt(), order = nextOrder))
        }
    }

    suspend fun updateTopicOrders(topics: List<ITopic>) {
        withContext(Dispatchers.IO) {
            val orders = topics.mapIndexed { index, iTopic ->
                TopicOrder(refTopicId = iTopic.id, order = index)
            }
            topicOrderDao.replaceAllOrders(orders)
        }
    }

    private suspend fun parseEntityToModel(entities: List<TopicEntry>): List<ITopic> {
        val topics = mutableListOf<ITopic>()
        entities.forEach { item ->
            when (item.type) {
                ITopic.TYPE_DIARY -> {
                    topics.add(
                        Diary(
                            id = item.id,
                            title = item.title,
                            color = item.color,
                            count = diaryDao.getCountByTopicId(item.id)
                        )
                    )
                }

                ITopic.TYPE_MEMO -> {
                    topics.add(
                        Memo(
                            id = item.id,
                            title = item.title,
                            color = item.color,
                            count = memoDao.getCountByTopicId(item.id)
                        )
                    )
                }

                ITopic.TYPE_CONTACTS -> {
                    topics.add(
                        Contacts(
                            id = item.id,
                            title = item.title,
                            color = item.color,
                            count = contactDao.getCountByTopicId(item.id)
                        )
                    )
                }
            }
        }
        return topics
    }
}
