package meow.softer.mydiary.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import meow.softer.mydiary.data.local.db.dao.ContactDao
import meow.softer.mydiary.data.local.db.dao.DiaryDao
import meow.softer.mydiary.data.local.db.dao.DiaryItemDao
import meow.softer.mydiary.data.local.db.dao.MemoDao
import meow.softer.mydiary.data.local.db.dao.MemoOrderDao
import meow.softer.mydiary.data.local.db.dao.TopicDao
import meow.softer.mydiary.data.local.db.entity.ContactEntry
import meow.softer.mydiary.data.local.db.entity.DiaryEntry
import meow.softer.mydiary.data.local.db.entity.DiaryItem
import meow.softer.mydiary.data.local.db.entity.MemoEntry
import meow.softer.mydiary.data.local.db.entity.MemoOrder
import meow.softer.mydiary.data.local.db.entity.TopicEntry
import meow.softer.mydiary.data.local.db.entity.TopicOrder
import meow.softer.mydiary.data.local.db.dao.TopicOrderDao

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