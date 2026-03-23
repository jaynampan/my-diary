package meow.softer.mydiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import meow.softer.mydiary.data.local.db.entity.TopicOrder

@Dao
interface TopicOrderDao {
    @Query("SELECT * FROM topic_order")
    fun getAll(): List<TopicOrder>

    @Query("SELECT * FROM topic_order WHERE ref_topic_id = :id")
    fun getByTopicId(id: Int): TopicOrder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(topicOrder: TopicOrder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAll(topicOrders: List<TopicOrder>)

    @Update
    fun update(topicOrder: TopicOrder)

    @Update
    fun updateAll(vararg topicOrder: TopicOrder)

    @Delete
    fun delete(topicOrder: TopicOrder)

    @Delete
    fun deleteAll(vararg topicOrder: TopicOrder)

    @Query("DELETE FROM topic_order")
    fun clearAll()

    @Transaction
    fun replaceAllOrders(topicOrders: List<TopicOrder>) {
        clearAll()
        addAll(topicOrders)
    }
}