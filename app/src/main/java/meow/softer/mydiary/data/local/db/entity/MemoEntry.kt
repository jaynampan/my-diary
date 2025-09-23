package meow.softer.mydiary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_entry")
data class MemoEntry(
    @PrimaryKey val id: Int,
    val content: String,
    val checked: Boolean = false,
    @ColumnInfo(name = "ref_topic_id") val refTopicId: Int
)
