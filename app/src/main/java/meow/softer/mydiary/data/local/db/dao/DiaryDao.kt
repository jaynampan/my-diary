package meow.softer.mydiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.local.db.entity.DiaryEntry

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entry")
    suspend fun getAll(): List<DiaryEntry>

    @Query("SELECT * FROM diary_entry WHERE ref_topic_id = :topicId")
    suspend fun getAllByTopicId(topicId: Int): List<DiaryEntry>

    @Query("SELECT COUNT(*) FROM diary_entry WHERE ref_topic_id = :topicId")
    suspend fun getCountByTopicId(topicId: Int): Int

    @Query("SELECT * FROM diary_entry WHERE id = :id")
    suspend fun getById(id: Int): DiaryEntry?

    @Query("SELECT * FROM diary_entry WHERE time LIKE :time")
    suspend fun getByTime(time: String): List<DiaryEntry>

    @Query("SELECT * FROM diary_entry WHERE title LIKE :title")
    suspend fun getByTitle(title: String): List<DiaryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(diaryEntry: DiaryEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(vararg diaryEntry: DiaryEntry)

    @Update
    suspend fun update(diaryEntry: DiaryEntry)

    @Update
    suspend fun updateAll(vararg diaryEntry: DiaryEntry)

    @Delete
    suspend fun delete(diaryEntry: DiaryEntry)

    @Delete
    suspend fun deleteAll(vararg diaryEntry: DiaryEntry)
}