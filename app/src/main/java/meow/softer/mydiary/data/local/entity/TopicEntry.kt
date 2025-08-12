package meow.softer.mydiary.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_entry")
data class TopicEntry(
    @PrimaryKey val id: Int,
    val name: String,
    val type: Int,
    val subtitle: String?,
    val color: String?
)
