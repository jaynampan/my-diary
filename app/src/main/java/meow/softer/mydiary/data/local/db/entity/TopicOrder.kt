package meow.softer.mydiary.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_order")
data class TopicOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val order: Int,
    @ColumnInfo(name = "ref_topic_id") val refTopicId: Int
)
