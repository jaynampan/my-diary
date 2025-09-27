package meow.softer.mydiary.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import meow.softer.mydiary.data.local.db.dao.ContactDao
import meow.softer.mydiary.data.local.db.dao.DiaryDao
import meow.softer.mydiary.data.local.db.dao.DiaryItemDao
import meow.softer.mydiary.data.local.db.dao.MemoDao
import meow.softer.mydiary.data.local.db.dao.MemoOrderDao
import meow.softer.mydiary.data.local.db.dao.TopicDao
import meow.softer.mydiary.data.local.db.dao.TopicOrderDao
import meow.softer.mydiary.data.local.db.entity.ContactEntry
import meow.softer.mydiary.data.local.db.entity.DiaryEntry
import meow.softer.mydiary.data.local.db.entity.DiaryItem
import meow.softer.mydiary.data.local.db.entity.MemoEntry
import meow.softer.mydiary.data.local.db.entity.MemoOrder
import meow.softer.mydiary.data.local.db.entity.TopicEntry
import meow.softer.mydiary.data.local.db.entity.TopicOrder
import meow.softer.mydiary.ui.models.ITopic

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

    companion object {
        @Volatile
        private var INSTANCE: DiaryDatabase? = null
        private const val DB_NAME = "my_diary.db"

        fun getDatabase(context: Context): DiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DiaryDatabase::class.java,
                    DB_NAME
                ).addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            InitialDataSQLs.forEach {
                                db.execSQL(it)
                            }
                        }
                    }
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

const val mitsuhaMemoId = 1
const val takiMemoId = 2
val InitialDataSQLs = listOf(
"insert into topic_entry(id,name,type) values($mitsuhaMemoId,\'ゼッタイ禁止\',${ITopic.TYPE_MEMO});",
"insert into topic_entry(id,name,type) values($takiMemoId,\'禁止事項 Ver.5\',${ITopic.TYPE_MEMO});",

"insert into memo_entry(id,content,checked,ref_topic_id) values(1,\'女子にも触るな！\',false,$mitsuhaMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(2,\'男子に触るな！\',false,$mitsuhaMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(3,\'脚をひらくな！\',true,$mitsuhaMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(4,\'体は見ない！/触らない！！\',false,$mitsuhaMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(5,\'お風呂ぜっっったい禁止！！！！！！！\',true,$mitsuhaMemoId);",

"insert into memo_entry(id,content,checked,ref_topic_id) values(6,\'司とベタベタするな.....\', true, $takiMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(7,\'奧寺先輩と馴れ馴れしくするな.....\', true, $takiMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(8,\'女言葉NG！\', false, $takiMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(9,\'遅刻するな！\', true, $takiMemoId);",
"insert into memo_entry(id,content,checked,ref_topic_id) values(10,\'無駄つかい禁止！\', false, $takiMemoId);")