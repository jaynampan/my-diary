package meow.softer.mydiary.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.provider.BaseColumns
import meow.softer.mydiary.data.db.DBStructure.ContactsEntry
import meow.softer.mydiary.data.db.DBStructure.DiaryEntry
import meow.softer.mydiary.data.db.DBStructure.DiaryEntry_V2
import meow.softer.mydiary.data.db.DBStructure.DiaryItemEntry_V2
import meow.softer.mydiary.data.db.DBStructure.MemoEntry
import meow.softer.mydiary.data.db.DBStructure.MemoOrderEntry
import meow.softer.mydiary.data.db.DBStructure.TopicEntry
import meow.softer.mydiary.data.db.DBStructure.TopicOrderEntry
import meow.softer.mydiary.entries.diary.item.IDiaryRow

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TOPIC_ENTRIES)
        db.execSQL(SQL_CREATE_TOPIC_ORDER)

        //Diary V2 work from db version 4
        db.execSQL(SQL_CREATE_DIARY_ENTRIES_V2)
        db.execSQL(SQL_CREATE_DIARY_ITEM_ENTRIES_V2)

        //Add memo order table in version 6
        db.execSQL(SQL_CREATE_MEMO_ENTRIES)
        db.execSQL(SQL_CREATE_MEMO_ORDER)

        db.execSQL(SQL_CREATE_CONTACTS_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        var oldVersion = oldVersion
        if (newVersion > oldVersion) {
            try {
                db.beginTransaction()
                if (oldVersion < 2) {
                    oldVersion++
                    val addLocationSql =
                        "ALTER TABLE  " + DiaryEntry.TABLE_NAME + " ADD COLUMN " + DiaryEntry.COLUMN_LOCATION + " " + TEXT_TYPE
                    val addTopicOrderSql =
                        "ALTER TABLE  " + TopicEntry.TABLE_NAME + " ADD COLUMN " + TopicEntry.COLUMN_ORDER + " " + INTEGER_TYPE
                    db.execSQL(addLocationSql)
                    db.execSQL(addTopicOrderSql)
                    db.execSQL(SQL_CREATE_MEMO_ENTRIES)
                }
                if (oldVersion < 3) {
                    //SubTitle for topic only
                    val addTopicSubtitleSql =
                        "ALTER TABLE  " + TopicEntry.TABLE_NAME + " ADD COLUMN " + TopicEntry.COLUMN_SUBTITLE + " " + TEXT_TYPE
                    db.execSQL(addTopicSubtitleSql)
                    db.execSQL(SQL_CREATE_CONTACTS_ENTRIES)
                }
                if (oldVersion < 4) {
                    //Create  diary V2 db
                    db.execSQL(SQL_CREATE_DIARY_ENTRIES_V2)
                    db.execSQL(SQL_CREATE_DIARY_ITEM_ENTRIES_V2)
                    //Move the old diaryContent to DiaryItemEntry_V2
                    version4MoveData(db)
                    //Delete  diary v1 db
                    val deleteV1DiaryTable = "DROP TABLE IF EXISTS " + DiaryEntry.TABLE_NAME
                    db.execSQL(deleteV1DiaryTable)
                }
                if (oldVersion < 5) {
                    //Add textcolor COLUMN
                    val addTopicTextColorSql =
                        "ALTER TABLE  " + TopicEntry.TABLE_NAME + " ADD COLUMN " + TopicEntry.COLUMN_COLOR + " " + INTEGER_TYPE
                    db.execSQL(addTopicTextColorSql)
                    //set textcolor default black color
                    version5AddTextColor(db)
                }
                //Memo order function work in version 25 & db version 6
                if (oldVersion < 6) {
                    db.execSQL(SQL_CREATE_MEMO_ORDER)
                    version6AddMemoOrder(db)
                }
                //Topic order method work in version 27 & db version 27
                if (oldVersion < 7) {
                    db.execSQL(SQL_CREATE_TOPIC_ORDER)
                    version7AddTopicOrder(db)
                }

                //Check update success
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        } else {
            onCreate(db)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
        onUpgrade(db, oldVersion, newVersion)
    }

    private fun version5AddTextColor(db: SQLiteDatabase) {
        val values = ContentValues()
        values.put(TopicEntry.COLUMN_COLOR, Color.BLACK)
        db.update(TopicEntry.TABLE_NAME, values, null, null)
    }


    private fun version4MoveData(db: SQLiteDatabase) {
        val dbManager = DBManager(db)
        //Copy old diary into new diary_v2
        val copyOldDiaryToV2 = "INSERT INTO " + DiaryEntry_V2.TABLE_NAME + " (" +
                BaseColumns._ID + COMMA_SEP +
                DiaryEntry_V2.COLUMN_TIME + COMMA_SEP +
                DiaryEntry_V2.COLUMN_TITLE + COMMA_SEP +
                DiaryEntry_V2.COLUMN_MOOD + COMMA_SEP +
                DiaryEntry_V2.COLUMN_WEATHER + COMMA_SEP +
                DiaryEntry_V2.COLUMN_ATTACHMENT + COMMA_SEP +
                DiaryEntry_V2.COLUMN_REF_TOPIC__ID + COMMA_SEP +
                DiaryEntry_V2.COLUMN_LOCATION + ")" +
                " SELECT " +
                BaseColumns._ID + COMMA_SEP +
                DiaryEntry.COLUMN_TIME + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_MOOD + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_WEATHER + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_ATTACHMENT + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_LOCATION + TEXT_TYPE +
                " FROM " + DiaryEntry.TABLE_NAME

        db.execSQL(copyOldDiaryToV2)


        //Old content add into diaryitem_v2
        val oldDiaryCursor = dbManager.selectAllV1Diary()
        for (i in 0..<oldDiaryCursor.count) {
            //Old version , it is only diaryText , and only 1 row
            dbManager.insertDiaryContent(
                IDiaryRow.TYPE_TEXT, 0,
                oldDiaryCursor.getString(3), oldDiaryCursor.getLong(0)
            )
            oldDiaryCursor.moveToNext()
        }
    }


    private fun version6AddMemoOrder(db: SQLiteDatabase?) {
        val dbUpdateTool = DBUpdateTool(db!!)
        // init order value = memo id
        val topicCursor = dbUpdateTool.version_6_SelectTopic()
        for (i in 0..<topicCursor.count) {
            val topicId = topicCursor.getLong(0)
            val memoCursor = dbUpdateTool.version_6_SelectMemo(topicId)
            for (j in 0..<memoCursor.count) {
                val memoId = memoCursor.getLong(0)
                dbUpdateTool.version_6_InsertMemoOrder(topicId, memoId, j.toLong())
                memoCursor.moveToNext()
            }
            memoCursor.close()
            topicCursor.moveToNext()
        }
        topicCursor.close()
    }

    private fun version7AddTopicOrder(db: SQLiteDatabase?) {
        val dbUpdateTool = DBUpdateTool(db!!)
        // init order value = memo id
        val topicCursor = dbUpdateTool.version_7_SelectTopic()
        for (i in 0..<topicCursor.count) {
            val topicId = topicCursor.getLong(0)
            dbUpdateTool.version_7_InsertTopicOrder(topicId, i.toLong())
            topicCursor.moveToNext()
        }
        topicCursor.close()
    }

    companion object {
        const val DATABASE_VERSION: Int = 7
        const val DATABASE_NAME: String = "mydiary.db"

        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"

        private const val COMMA_SEP = ","
        private const val FOREIGN = " FOREIGN KEY "
        private const val REFERENCES = " REFERENCES "
        private val SQL_CREATE_TOPIC_ENTRIES = "CREATE TABLE " + TopicEntry.TABLE_NAME + " (" +
                BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                TopicEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_COLOR + INTEGER_TYPE +
                " )"

        private val SQL_CREATE_TOPIC_ORDER = "CREATE TABLE " + TopicOrderEntry.TABLE_NAME + " (" +
                TopicOrderEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                TopicOrderEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                FOREIGN + " (" + TopicOrderEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                ")"

        /**
         * Discarded DIARY DB
         */
        private val SQL_CREATE_DIARY_ENTRIES = "CREATE TABLE " + DiaryEntry.TABLE_NAME + " (" +
                BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                DiaryEntry.COLUMN_TIME + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_MOOD + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_WEATHER + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_ATTACHMENT + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                DiaryEntry.COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
                FOREIGN + " (" + DiaryEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                " )"

        private val SQL_CREATE_DIARY_ENTRIES_V2 =
            "CREATE TABLE " + DiaryEntry_V2.TABLE_NAME + " (" +
                    BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DiaryEntry_V2.COLUMN_TIME + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_MOOD + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_WEATHER + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_ATTACHMENT + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                    DiaryEntry_V2.COLUMN_LOCATION + TEXT_TYPE + COMMA_SEP +
                    FOREIGN + " (" + DiaryEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                    " )"

        private val SQL_CREATE_DIARY_ITEM_ENTRIES_V2 =
            "CREATE TABLE " + DiaryItemEntry_V2.TABLE_NAME + " (" +
                    BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DiaryItemEntry_V2.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_POSITION + INTEGER_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN + " (" + DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + ")" + REFERENCES + DiaryEntry_V2.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                    " )"

        private val SQL_CREATE_MEMO_ENTRIES = "CREATE TABLE " + MemoEntry.TABLE_NAME + " (" +
                BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                MemoEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                MemoEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                MemoEntry.COLUMN_CHECKED + INTEGER_TYPE + COMMA_SEP +
                MemoEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                FOREIGN + " (" + MemoEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                " )"

        private val SQL_CREATE_MEMO_ORDER = "CREATE TABLE " + MemoOrderEntry.TABLE_NAME + " (" +
                MemoOrderEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                MemoOrderEntry.COLUMN_REF_MEMO__ID + INTEGER_TYPE + COMMA_SEP +
                MemoOrderEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                FOREIGN + " (" + MemoOrderEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + MemoEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" + COMMA_SEP +
                FOREIGN + " (" + MemoOrderEntry.COLUMN_REF_MEMO__ID + ")" + REFERENCES + MemoEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                " )"

        private val SQL_CREATE_CONTACTS_ENTRIES =
            "CREATE TABLE " + ContactsEntry.TABLE_NAME + " (" +
                    BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    ContactsEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_PHONENUMBER + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_PHOTO + TEXT_TYPE + COMMA_SEP +
                    ContactsEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN + " (" + ContactsEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                    " )"
    }
}
