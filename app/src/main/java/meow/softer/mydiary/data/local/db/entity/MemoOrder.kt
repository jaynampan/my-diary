package meow.softer.mydiary.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_order")
data class MemoOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val order: Int,
    @ColumnInfo(name = "ref_memo_id") val refMemoId: Int // memo entry id
)
