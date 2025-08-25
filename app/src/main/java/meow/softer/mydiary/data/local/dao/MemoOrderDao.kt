package meow.softer.mydiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.entity.MemoOrder

@Dao
interface MemoOrderDao {
    @Query("SELECT * FROM memo_order")
    suspend fun getAll(): List<MemoOrder>

    @Query("SELECT * FROM memo_order WHERE id = :id")
    suspend fun getById(id: Int): MemoOrder

    @Insert
    suspend fun add(memoOrder: MemoOrder)

    @Insert
    suspend fun addAll(vararg memoOrder: MemoOrder)

    @Update
    suspend fun update(memoOrder: MemoOrder)

    @Update
    suspend fun updateAll(vararg memoOrder: MemoOrder)

    @Delete
    suspend fun delete(memoOrder: MemoOrder)

    @Delete
    suspend fun deleteAll(vararg memoOrder: MemoOrder)

}