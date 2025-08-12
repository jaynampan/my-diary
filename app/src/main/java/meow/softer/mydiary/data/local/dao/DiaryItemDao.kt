package meow.softer.mydiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.entity.DiaryItem

@Dao
interface DiaryItemDao {
    @Query("SELECT * FROM diary_item")
    suspend fun getAll(): List<DiaryItem>
    @Query("SELECT * FROM diary_item WHERE ref_diary_id = :diaryId")
    suspend fun getAllByDiaryId(diaryId: Int): List<DiaryItem>

    @Query("SELECT * FROM diary_item WHERE id = :id")
    suspend fun getById(id: Int): DiaryItem

    @Insert
    suspend fun add(diaryItem: DiaryItem)

    @Insert
    suspend fun addAll(vararg diaryItem: DiaryItem)

    @Update
    suspend fun update(diaryItem: DiaryItem)

    @Update
    suspend fun updateAll(vararg diaryItem: DiaryItem)

    @Delete
    suspend fun delete(diaryItem: DiaryItem)

    @Delete
    suspend fun deleteAll(vararg diaryItem: DiaryItem)

}