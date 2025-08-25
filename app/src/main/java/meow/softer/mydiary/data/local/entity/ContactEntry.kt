package meow.softer.mydiary.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_entry")
data class ContactEntry(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "ref_topic_id") val refTopicId: Int,
    val name: String,
    val photo: String?
)