package meow.softer.mydiary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_order")
data class MemoOrder(
    @PrimaryKey val id: Int,
    val order: Int,
    @ColumnInfo(name = "ref_topic_id") val refTopicId: Int,
    @ColumnInfo(name = "ref_memo_id") val refMemoId: Int
)
