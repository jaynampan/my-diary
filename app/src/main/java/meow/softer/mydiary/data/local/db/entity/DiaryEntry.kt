package meow.softer.mydiary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entry")
data class DiaryEntry(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "ref_topic_id") val refTopicId: Int,
    val time: Int,
    val title: String,
    val mood: Int?,
    val weather: Int?,
    val attachment: String?,
    val location: String?
)

