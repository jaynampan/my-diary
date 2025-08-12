package meow.softer.mydiary.data.local.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import meow.softer.mydiary.data.local.db.DBStructure.TopicEntry

class DBUpdateTool(private val db: SQLiteDatabase) {
    /*
        * Version 6
        */
    fun version_6_SelectMemo(topicId: Long): Cursor {
        val c = db.query(
            DBStructure.MemoEntry.TABLE_NAME,
            null,
            DBStructure.MemoEntry.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString()),
            null,
            null,
            null,
            null
        )
        if (c != null) {
            c.moveToFirst()
        }
        return c
    }

    fun version_6_InsertMemoOrder(topicId: Long, memoId: Long, order: Long): Long {
        val values = ContentValues()
        values.put(DBStructure.MemoOrderEntry.COLUMN_ORDER, order)
        values.put(DBStructure.MemoOrderEntry.COLUMN_REF_TOPIC__ID, topicId)
        values.put(DBStructure.MemoOrderEntry.COLUMN_REF_MEMO__ID, memoId)
        return db.insert(
            DBStructure.MemoOrderEntry.TABLE_NAME,
            null,
            values
        )
    }

    /**
     * Old selectTopic method
     * @return
     */
    fun version_6_SelectTopic(): Cursor {
        val c = db.query(
            TopicEntry.TABLE_NAME, null, null, null, null, null,
            BaseColumns._ID + " DESC"
        )
        if (c != null) {
            c.moveToFirst()
        }
        return c
    }

    /*
     * Version 7
     */
    fun version_7_InsertTopicOrder(topicId: Long, order: Long): Long {
        val values = ContentValues()
        values.put(DBStructure.TopicOrderEntry.COLUMN_ORDER, order)
        values.put(DBStructure.TopicOrderEntry.COLUMN_REF_TOPIC__ID, topicId)
        return db.insert(
            DBStructure.TopicOrderEntry.TABLE_NAME,
            null,
            values
        )
    }

    fun version_7_SelectTopic(): Cursor {
        val c = db.query(
            TopicEntry.TABLE_NAME, null, null, null, null, null,
            BaseColumns._ID + " ASC"
        )
        if (c != null) {
            c.moveToFirst()
        }
        return c
    }
}
