package meow.softer.mydiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.entity.TopicOrder

@Dao
interface TopicOrderDao {
    @Query("SELECT * FROM topic_order")
    fun getAll(): List<TopicOrder>

    @Query("SELECT * FROM topic_order WHERE id = :id")
    fun getById(id: Int): TopicOrder

    @Insert
    fun add(topicOrder: TopicOrder)

    @Insert
    fun addAll(vararg topicOrder: TopicOrder)

    @Update
    fun update(topicOrder: TopicOrder)

    @Update
    fun updateAll(vararg topicOrder: TopicOrder)

    @Delete
    fun delete(topicOrder: TopicOrder)

    @Delete
    fun deleteAll(vararg topicOrder: TopicOrder)

}