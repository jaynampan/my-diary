package meow.softer.mydiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.entity.TopicEntry


@Dao
interface TopicDao {
    @Query("SELECT * FROM topic_entry")
    fun getAll(): List<TopicEntry>

    @Query("SELECT * FROM topic_entry WHERE id = :id")
    fun getById(id: Int): TopicEntry

    @Insert
    fun insert(topicEntry: TopicEntry)

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