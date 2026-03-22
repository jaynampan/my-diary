package meow.softer.mydiary.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_order")
data class TopicOrder(
    @PrimaryKey @ColumnInfo(name = "ref_topic_id") val refTopicId: Int,
    val order: Int
)
