package meow.softer.mydiary.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_item")
data class DiaryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val type: Int,
    val position: Int,
    val content: String,
    @ColumnInfo(name = "ref_diary_id") val refDiaryId: Int

)
