package meow.softer.mydiary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_item")
data class DiaryItem(
    @PrimaryKey val id: Int,
    val type: Int,
    val position: Int,
    val content: String,
    @ColumnInfo(name = "ref_diary_id") val refDiaryId: Int

)
