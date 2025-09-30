package meow.softer.mydiary.data.local.db.entity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topic_entry")
data class TopicEntry(
    @PrimaryKey val id: Int,
    val title: String,
    val subtitle: String?,
    val type: Int,
    val color: Int = Color.Black.toArgb()
)
