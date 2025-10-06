package meow.softer.mydiary.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memo_entry")
data class MemoEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val content: String,
    val checked: Boolean = false,
    @ColumnInfo(name = "ref_topic_id") val refTopicId: Int
)
