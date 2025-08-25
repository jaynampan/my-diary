package meow.softer.mydiary.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import meow.softer.mydiary.data.local.db.DBStructure.ContactsEntry
import meow.softer.mydiary.data.local.db.DBStructure.DiaryEntry
import meow.softer.mydiary.data.local.db.DBStructure.DiaryEntry_V2
import meow.softer.mydiary.data.local.db.DBStructure.DiaryItemEntry_V2
import meow.softer.mydiary.data.local.db.DBStructure.MemoEntry
import meow.softer.mydiary.data.local.db.DBStructure.MemoOrderEntry
import meow.softer.mydiary.data.local.db.DBStructure.TopicEntry
import meow.softer.mydiary.data.local.db.DBStructure.TopicOrderEntry

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

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // the database version is 1. no need to upgrade for now
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
        onUpgrade(db, oldVersion, newVersion)
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


    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "mydiary.db"

        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"

        private const val COMMA_SEP = ","
        private const val FOREIGN = " FOREIGN KEY "
        private const val REFERENCES = " REFERENCES "
        private const val SQL_CREATE_TOPIC_ENTRIES = "CREATE TABLE " + TopicEntry.TABLE_NAME + " (" +
                BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                TopicEntry.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_SUBTITLE + TEXT_TYPE + COMMA_SEP +
                TopicEntry.COLUMN_COLOR + INTEGER_TYPE +
                " )"

        private const val SQL_CREATE_TOPIC_ORDER = "CREATE TABLE " + TopicOrderEntry.TABLE_NAME + " (" +
                TopicOrderEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                TopicOrderEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                FOREIGN + " (" + TopicOrderEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                ")"


        private const val SQL_CREATE_DIARY_ENTRIES_V2 =
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

        private const val SQL_CREATE_DIARY_ITEM_ENTRIES_V2 =
            "CREATE TABLE " + DiaryItemEntry_V2.TABLE_NAME + " (" +
                    BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    DiaryItemEntry_V2.COLUMN_TYPE + INTEGER_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_POSITION + INTEGER_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                    DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + INTEGER_TYPE + COMMA_SEP +
                    FOREIGN + " (" + DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + ")" + REFERENCES + DiaryEntry_V2.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                    " )"

        private const val SQL_CREATE_MEMO_ENTRIES = "CREATE TABLE " + MemoEntry.TABLE_NAME + " (" +
                BaseColumns._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                MemoEntry.COLUMN_ORDER + INTEGER_TYPE + COMMA_SEP +
                MemoEntry.COLUMN_CONTENT + TEXT_TYPE + COMMA_SEP +
                MemoEntry.COLUMN_CHECKED + INTEGER_TYPE + COMMA_SEP +
                MemoEntry.COLUMN_REF_TOPIC__ID + INTEGER_TYPE + COMMA_SEP +
                FOREIGN + " (" + MemoEntry.COLUMN_REF_TOPIC__ID + ")" + REFERENCES + TopicEntry.TABLE_NAME + "(" + BaseColumns._ID + ")" +
                " )"

        private const val SQL_CREATE_MEMO_ORDER = "CREATE TABLE " + MemoOrderEntry.TABLE_NAME + " (" +
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
