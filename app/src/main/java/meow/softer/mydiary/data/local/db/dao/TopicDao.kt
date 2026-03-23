package meow.softer.mydiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.local.db.entity.TopicEntry


@Dao
interface TopicDao {
    @Query("SELECT * FROM topic_entry")
    fun getAll(): List<TopicEntry>

    @Query("SELECT topic_entry.* FROM topic_entry LEFT JOIN topic_order ON topic_entry.id = topic_order.ref_topic_id ORDER BY CASE WHEN topic_order.`order` IS NULL THEN 999 ELSE topic_order.`order` END ASC")
    fun getAllOrdered(): List<TopicEntry>

    @Query("SELECT topic_entry.* FROM topic_entry LEFT JOIN topic_order ON topic_entry.id = topic_order.ref_topic_id WHERE topic_entry.title LIKE :query ORDER BY CASE WHEN topic_order.`order` IS NULL THEN 999 ELSE topic_order.`order` END ASC")
    fun search(query: String): List<TopicEntry>

    @Query("SELECT * FROM topic_entry WHERE id = :id")
    fun getById(id: Int): TopicEntry

    @Insert
    fun insert(topicEntry: TopicEntry): Long

    @Insert
    fun insertAll(vararg topicEntry: TopicEntry)

    @Update
    fun update(topicEntry: TopicEntry)

    @Update
    fun updateAll(vararg topicEntry: TopicEntry)

    @Delete
    fun delete(topicEntry: TopicEntry)

    @Delete
    fun deleteAll(vararg topicEntry: TopicEntry)

}
