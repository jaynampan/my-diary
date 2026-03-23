package meow.softer.mydiary.data.local.db

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import meow.softer.mydiary.ui.models.DiaryInfoHelper
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

const val mitsuhaMemoId = 1
const val takiMemoId = 2
const val sampleDiaryId = 3
val defaultColor = Color.Black.toArgb()
val InitialDataSQLs = listOf(
    "insert into topic_entry(id,title,type,color) values($mitsuhaMemoId,\'ゼッタイ禁止\',${ITopic.TYPE_MEMO},$defaultColor);",
    "insert into topic_entry(id,title,type,color) values($takiMemoId,\'禁止事項 Ver.5\',${ITopic.TYPE_MEMO},$defaultColor);",
    "insert into topic_entry(id,title,type,color) values($sampleDiaryId, \'Dairy\',${ITopic.TYPE_DIARY},$defaultColor)",

    "insert into topic_order(`order`,ref_topic_id) values(0, $takiMemoId);",
    "insert into topic_order(`order`,ref_topic_id) values(1, $mitsuhaMemoId);",
    "insert into topic_order(`order`,ref_topic_id) values(3, $sampleDiaryId);",

    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(1,\'女子にも触るな！\',0,$mitsuhaMemoId,0);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(2,\'男子に触るな！\',0,$mitsuhaMemoId,2);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(3,\'脚をひらくな！\',1,$mitsuhaMemoId,1);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(4,\'体は見ない！/触らない！！\',0,$mitsuhaMemoId,3);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(5,\'お風呂ぜっっったい禁止！！！！！！！\',1,$mitsuhaMemoId,4);",

    "insert into memo_order(`order`,ref_memo_id) values(0,1);",
    "insert into memo_order(`order`,ref_memo_id) values(2,2);",
    "insert into memo_order(`order`,ref_memo_id) values(1,3);",
    "insert into memo_order(`order`,ref_memo_id) values(3,4);",
    "insert into memo_order(`order`,ref_memo_id) values(4,5);",

    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(6,\'司とベタベタするな.....\', 1, $takiMemoId,4);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(7,\'奧寺先輩と馴れ馴れしくするな.....\', 1, $takiMemoId,1);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(8,\'女言葉NG！\', 0, $takiMemoId,3);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(9,\'遅刻するな！\', 1, $takiMemoId,2);",
    "insert into memo_entry(id,content,checked,ref_topic_id,position) values(10,\'無駄つかい禁止！\', 0, $takiMemoId,0);",

    "insert into memo_order(`order`,ref_memo_id) values(4,6);",
    "insert into memo_order(`order`,ref_memo_id) values(1,7);",
    "insert into memo_order(`order`,ref_memo_id) values(3,8);",
    "insert into memo_order(`order`,ref_memo_id) values(2,9);",
    "insert into memo_order(`order`,ref_memo_id) values(0,10);",

    "insert into diary_entry(id,ref_topic_id,time,title,mood,weather,location) " +
            "values(1,$sampleDiaryId,${1475665800},\'東京生活3❤\',${DiaryInfoHelper.MOOD_HAPPY}," +
            "${DiaryInfoHelper.WEATHER_RAINY},\'Tokyo\')",
)
