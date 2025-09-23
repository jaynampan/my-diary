package meow.softer.mydiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import meow.softer.mydiary.data.local.dao.ContactDao
import meow.softer.mydiary.data.local.dao.DiaryDao
import meow.softer.mydiary.data.local.dao.DiaryItemDao
import meow.softer.mydiary.data.local.dao.MemoDao
import meow.softer.mydiary.data.local.dao.MemoOrderDao
import meow.softer.mydiary.data.local.dao.TopicDao
import meow.softer.mydiary.data.entity.ContactEntry
import meow.softer.mydiary.data.entity.DiaryEntry
import meow.softer.mydiary.data.entity.DiaryItem
import meow.softer.mydiary.data.entity.MemoEntry
import meow.softer.mydiary.data.entity.MemoOrder
import meow.softer.mydiary.data.entity.TopicEntry
import meow.softer.mydiary.data.entity.TopicOrder
import meow.softer.mydiary.data.local.dao.TopicOrderDao

const val DatabaseVersion = 1

@Database(
    entities = [
        DiaryEntry::class,
        ContactEntry::class,
        DiaryItem::class,
        MemoEntry::class,
        MemoOrder::class,
        TopicEntry::class,
        TopicOrder::class
    ],
    version = DatabaseVersion
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun diaryDao(): DiaryDao
    abstract fun memoDao(): MemoDao
    abstract fun topicDao(): TopicDao
    abstract fun diaryItemDao(): DiaryItemDao
    abstract fun memoOrderDao(): MemoOrderDao
    abstract fun topicOrderDao(): TopicOrderDao
}