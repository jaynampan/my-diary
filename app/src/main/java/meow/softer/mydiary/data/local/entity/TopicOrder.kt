package meow.softer.mydiary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_order")
data class TopicOrder(
    @PrimaryKey val id: Int,
    val order: Int,
    @ColumnInfo(name = "ref_topic_id") val refTopicId: Int
)
