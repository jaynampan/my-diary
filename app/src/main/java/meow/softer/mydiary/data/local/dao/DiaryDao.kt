package meow.softer.mydiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.entity.DiaryEntry

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entry")
    suspend fun getAll(): List<DiaryEntry>

    @Query("SELECT * FROM diary_entry WHERE ref_topic_id = :topicId")
    suspend fun getAllByTopicId(topicId: Int): List<DiaryEntry>

    @Query("SELECT * FROM diary_entry WHERE id = :id")
    suspend fun getById(id: Int): DiaryEntry

    @Query("SELECT * FROM diary_entry WHERE time LIKE :time")
    suspend fun getByTime(time: String): List<DiaryEntry>

    @Query("SELECT * FROM diary_entry WHERE title LIKE :title")
    suspend fun getByTitle(title: String): List<DiaryEntry>

    @Insert
    suspend fun add(diaryEntry: DiaryEntry)

    @Insert
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