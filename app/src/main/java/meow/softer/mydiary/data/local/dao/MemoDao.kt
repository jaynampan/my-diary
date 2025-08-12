package meow.softer.mydiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.entity.MemoEntry

@Dao
interface MemoDao {
    @Query("SELECT * FROM memo_entry")
    suspend fun getAll(): List<MemoEntry>

    @Query("SELECT * FROM memo_entry WHERE ref_topic_id = :topicId")
    suspend fun getAllByTopicId(topicId: Int): List<MemoEntry>

    @Query("SELECT * FROM memo_entry WHERE id = :id")
    suspend fun getById(id: Int): MemoEntry

    @Insert
    suspend fun add(memoEntry: MemoEntry)

    @Insert
    suspend fun addAll(vararg memoEntry: MemoEntry)

    @Update
    suspend fun update(memoEntry: MemoEntry)

    @Update
    suspend fun updateAll(vararg memoEntry: MemoEntry)

    @Delete
    suspend fun delete(memoEntry: MemoEntry)

    @Delete
    suspend fun deleteAll(vararg memoEntry: MemoEntry)


}