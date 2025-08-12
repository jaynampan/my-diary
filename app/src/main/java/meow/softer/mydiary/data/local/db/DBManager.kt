package meow.softer.mydiary.data.local.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.provider.BaseColumns
import android.util.Log
import meow.softer.mydiary.data.local.db.DBStructure.ContactsEntry
import meow.softer.mydiary.data.local.db.DBStructure.DiaryEntry_V2
import meow.softer.mydiary.data.local.db.DBStructure.DiaryItemEntry_V2
import meow.softer.mydiary.data.local.db.DBStructure.MemoEntry
import meow.softer.mydiary.data.local.db.DBStructure.MemoOrderEntry
import meow.softer.mydiary.data.local.db.DBStructure.TopicEntry
import meow.softer.mydiary.data.local.db.DBStructure.TopicOrderEntry

class DBManager {
    private var context: Context? = null
    private var db: SQLiteDatabase? = null
    private var mDBHelper: DBHelper? = null

    constructor(context: Context?) {
        this.context = context
    }

    constructor(db: SQLiteDatabase) {
        this.db = db
    }

    /**
     * DB IO
     */
    @Throws(SQLiteException::class)
    fun openDB() {
        mDBHelper = DBHelper(context)
        // Gets the data repository in write mode
        this.db = mDBHelper!!.writableDatabase
    }

    fun closeDB() {
        mDBHelper!!.close()
    }


    fun beginTransaction() {
        db!!.beginTransaction()
    }

    fun setTransactionSuccessful() {
        db!!.setTransactionSuccessful()
    }

    fun endTransaction() {
        db!!.endTransaction()
    }

    /*
     * Topic
     */
    fun insertTopic(name: String?, type: Int, color: Int): Long {
        return db!!.insert(
            TopicEntry.TABLE_NAME,
            null,
            this.createTopicCV(name, type, color)
        )
    }

    fun insertTopicOrder(topicId: Long, order: Long): Long {
        val values = ContentValues()
        values.put(TopicOrderEntry.COLUMN_ORDER, order)
        values.put(TopicOrderEntry.COLUMN_REF_TOPIC__ID, topicId)
        return db!!.insert(
            TopicOrderEntry.TABLE_NAME,
            null,
            values
        )
    }

    fun updateTopic(topicId: Long, name: String?, color: Int): Long {
        val values = ContentValues()
        values.put(TopicEntry.COLUMN_NAME, name)
        values.put(TopicEntry.COLUMN_COLOR, color)
        return db!!.update(
            TopicEntry.TABLE_NAME,
            values,
            BaseColumns._ID + " = ?",
            arrayOf<String>(topicId.toString())
        ).toLong()
    }

    /**
     * Select Topic & order for show in Topic list
     *
     * @return
     */
    fun selectTopic(): Cursor {
        val c = db!!.rawQuery(
            ("SELECT * FROM " + TopicEntry.TABLE_NAME
                    + " LEFT OUTER JOIN " + TopicOrderEntry.TABLE_NAME
                    + " ON " + BaseColumns._ID + " = " + TopicOrderEntry.COLUMN_REF_TOPIC__ID
                    + " ORDER BY " + TopicOrderEntry.COLUMN_ORDER + " DESC "),
            null
        )
        if (c != null) {
            c.moveToFirst()
        }
        return c
    }

    fun deleteAllCurrentTopicOrder(): Long {
        return db!!.delete(
            TopicOrderEntry.TABLE_NAME,
            null, null
        ).toLong()
    }

    fun getDiaryCountByTopicId(topicId: Long): Int {
        val cursor = db!!.rawQuery(
            "SELECT COUNT (*) FROM " + DiaryEntry_V2.TABLE_NAME + " WHERE " + DiaryEntry_V2.COLUMN_REF_TOPIC__ID + "=?",
            arrayOf<String>(topicId.toString())
        )
        var count = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getMemoCountByTopicId(topicId: Long): Int {
        val cursor = db!!.rawQuery(
            "SELECT COUNT (*) FROM " + MemoEntry.TABLE_NAME + " WHERE " + MemoEntry.COLUMN_REF_TOPIC__ID + "=?",
            arrayOf<String>(topicId.toString())
        )
        var count = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun getContactsCountByTopicId(topicId: Long): Int {
        val cursor = db!!.rawQuery(
            "SELECT COUNT (*) FROM " + ContactsEntry.TABLE_NAME + " WHERE " + ContactsEntry.COLUMN_REF_TOPIC__ID + "=?",
            arrayOf<String>(topicId.toString())
        )
        var count = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun delTopic(topicId: Long): Long {
        return db!!.delete(
            TopicEntry.TABLE_NAME,
            BaseColumns._ID + " = ?",
            arrayOf<String>(topicId.toString())
        ).toLong()
    }

    private fun createTopicCV(name: String?, type: Int, color: Int): ContentValues {
        val values = ContentValues()
        values.put(TopicEntry.COLUMN_NAME, name)
        values.put(TopicEntry.COLUMN_TYPE, type)
        values.put(TopicEntry.COLUMN_COLOR, color)
        return values
    }

    /*
     * Diary
     */
    fun insertDiaryInfo(
        time: Long, title: String?,
        mood: Int, weather: Int, attachment: Boolean,
        refTopicId: Long, locationName: String?
    ): Long {
        return db!!.insert(
            DiaryEntry_V2.TABLE_NAME,
            null,
            this.createDiaryInfoCV(
                time, title,
                mood, weather, attachment, refTopicId, locationName
            )
        )
    }

    fun insertDiaryContent(type: Int, position: Int, content: String?, diaryId: Long): Long {
        return db!!.insert(
            DiaryItemEntry_V2.TABLE_NAME,
            null,
            this.createDiaryContentCV(type, position, content, diaryId)
        )
    }

    fun updateDiary(
        diaryId: Long, time: Long, title: String?,
        mood: Int, weather: Int, location: String?, attachment: Boolean
    ): Long {
        val values = ContentValues()
        values.put(DiaryEntry_V2.COLUMN_TIME, time)
        values.put(DiaryEntry_V2.COLUMN_TITLE, title)
        values.put(DiaryEntry_V2.COLUMN_MOOD, mood)
        values.put(DiaryEntry_V2.COLUMN_WEATHER, weather)
        values.put(DiaryEntry_V2.COLUMN_LOCATION, location)
        values.put(DiaryEntry_V2.COLUMN_ATTACHMENT, attachment)

        return db!!.update(
            DiaryEntry_V2.TABLE_NAME,
            values,
            BaseColumns._ID + " = ?",
            arrayOf<String>(diaryId.toString())
        ).toLong()
    }

    fun delDiary(diaryId: Long): Long {
        return db!!.delete(
            DiaryEntry_V2.TABLE_NAME,
            BaseColumns._ID + " = ?",
            arrayOf<String>(diaryId.toString())
        ).toLong()
    }

    fun delAllDiaryInTopic(topicId: Long): Long {
        return db!!.delete(
            DiaryEntry_V2.TABLE_NAME,
            DiaryEntry_V2.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString())
        ).toLong()
    }

    fun delAllDiaryItemByDiaryId(diaryId: Long): Long {
        return db!!.delete(
            DiaryItemEntry_V2.TABLE_NAME,
            DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + " = ?",
            arrayOf<String>(diaryId.toString())
        ).toLong()
    }


    fun selectDiaryList(topicId: Long): Cursor {
        val c = db!!.query(
            DiaryEntry_V2.TABLE_NAME,
            null,
            DiaryEntry_V2.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString()),
            null,
            null,
            DiaryEntry_V2.COLUMN_TIME + " DESC , " + BaseColumns._ID + " DESC",
            null
        )
        c.moveToFirst()
        return c
    }

    fun selectDiaryInfoByDiaryId(diaryId: Long): Cursor {
        val c = db!!.query(
            DiaryEntry_V2.TABLE_NAME,
            null,
            BaseColumns._ID + " = ?",
            arrayOf<String>(diaryId.toString()),
            null,
            null,
            null
        )
        c.moveToFirst()
        return c
    }

    fun selectDiaryContentByDiaryId(diaryId: Long): Cursor {
        val c = db!!.query(
            DiaryItemEntry_V2.TABLE_NAME,
            null,
            DiaryItemEntry_V2.COLUMN_REF_DIARY__ID + " = ?",
            arrayOf<String>(diaryId.toString()),
            null,
            null,
            DiaryItemEntry_V2.COLUMN_POSITION + " ASC",
            null
        )
        c.moveToFirst()
        return c
    }


    private fun createDiaryInfoCV(
        time: Long, title: String?,
        mood: Int, weather: Int, attachment: Boolean, refTopicId: Long,
        locationName: String?
    ): ContentValues {
        val values = ContentValues()
        values.put(DiaryEntry_V2.COLUMN_TIME, time)
        values.put(DiaryEntry_V2.COLUMN_TITLE, title)
        values.put(DiaryEntry_V2.COLUMN_MOOD, mood)
        values.put(DiaryEntry_V2.COLUMN_WEATHER, weather)
        values.put(DiaryEntry_V2.COLUMN_ATTACHMENT, attachment)
        values.put(DiaryEntry_V2.COLUMN_REF_TOPIC__ID, refTopicId)
        values.put(DiaryEntry_V2.COLUMN_LOCATION, locationName)
        return values
    }

    private fun createDiaryContentCV(
        type: Int,
        position: Int,
        content: String?,
        diaryId: Long
    ): ContentValues {
        val values = ContentValues()
        values.put(DiaryItemEntry_V2.COLUMN_TYPE, type)
        values.put(DiaryItemEntry_V2.COLUMN_POSITION, position)
        values.put(DiaryItemEntry_V2.COLUMN_CONTENT, content)
        values.put(DiaryItemEntry_V2.COLUMN_REF_DIARY__ID, diaryId)
        return values
    }

    /*
     * MEMO
     */
    fun insertMemo(content: String?, isChecked: Boolean, refTopicId: Long): Long {
        return db!!.insert(
            MemoEntry.TABLE_NAME,
            null,
            this.createMemoCV(content, isChecked, refTopicId)
        )
    }


    fun delMemo(memoId: Long): Long {
        return db!!.delete(
            MemoEntry.TABLE_NAME,
            BaseColumns._ID + " = ?",
            arrayOf<String>(memoId.toString())
        ).toLong()
    }

    fun delAllMemoInTopic(topicId: Long): Long {
        return db!!.delete(
            MemoEntry.TABLE_NAME,
            MemoEntry.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString())
        ).toLong()
    }

    /**
     * For select all memo and add order when database version update
     *
     * @param topicId
     * @return
     */
    fun selectMemo(topicId: Long): Cursor {
        val c = db!!.query(
            MemoEntry.TABLE_NAME,
            null,
            MemoEntry.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString()),
            null,
            null,
            null,
            null
        )
        c.moveToFirst()
        return c
    }

    /**
     * Select memo & order for show in memoActivity
     *
     * @param topicId
     * @return
     */
    fun selectMemoAndMemoOrder(topicId: Long): Cursor {
        val c = db!!.rawQuery(
            ("SELECT * FROM " + MemoEntry.TABLE_NAME
                    + " LEFT OUTER JOIN " + MemoOrderEntry.TABLE_NAME
                    + " ON " + BaseColumns._ID + " = " + MemoOrderEntry.COLUMN_REF_MEMO__ID
                    + " WHERE " + MemoEntry.COLUMN_REF_TOPIC__ID + " = " + topicId
                    + " ORDER BY " + MemoOrderEntry.COLUMN_ORDER + " DESC "),
            null
        )
        c.moveToFirst()
        return c
    }

    fun updateMemoChecked(memoId: Long, isChecked: Boolean): Long {
        val values = ContentValues()
        values.put(MemoEntry.COLUMN_CHECKED, isChecked)
        return db!!.update(
            MemoEntry.TABLE_NAME,
            values,
            BaseColumns._ID + " = ?",
            arrayOf<String>(memoId.toString())
        ).toLong()
    }

    fun updateMemoContent(memoId: Long, memoContent: String?): Long {
        val values = ContentValues()
        values.put(MemoEntry.COLUMN_CONTENT, memoContent)
        return db!!.update(
            MemoEntry.TABLE_NAME,
            values,
            BaseColumns._ID + " = ?",
            arrayOf<String>(memoId.toString())
        ).toLong()
    }

    fun insertMemoOrder(topicId: Long, memoId: Long, order: Long): Long {
        val values = ContentValues()
        values.put(MemoOrderEntry.COLUMN_ORDER, order)
        values.put(MemoOrderEntry.COLUMN_REF_TOPIC__ID, topicId)
        values.put(MemoOrderEntry.COLUMN_REF_MEMO__ID, memoId)
        return db!!.insert(
            MemoOrderEntry.TABLE_NAME,
            null,
            values
        )
    }

    fun deleteMemoOrder(memoId: Long): Long {
        return db!!.delete(
            MemoOrderEntry.TABLE_NAME,
            MemoOrderEntry.COLUMN_REF_MEMO__ID + " = ?",
            arrayOf<String>(memoId.toString())
        ).toLong()
    }

    fun deleteAllCurrentMemoOrder(topicId: Long): Long {
        return db!!.delete(
            MemoOrderEntry.TABLE_NAME,
            MemoOrderEntry.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString())
        ).toLong()
    }


    private fun createMemoCV(
        content: String?,
        isChecked: Boolean,
        refTopicId: Long
    ): ContentValues {
        val values = ContentValues()
        values.put(MemoEntry.COLUMN_CONTENT, content)
        values.put(MemoEntry.COLUMN_CHECKED, isChecked)
        values.put(MemoEntry.COLUMN_REF_TOPIC__ID, refTopicId)
        return values
    }

    /*
     * Contacts
     */
    fun insertContacts(
        name: String?,
        phoneNumber: String?,
        photo: String?,
        refTopicId: Long
    ): Long {
        return db!!.insert(
            ContactsEntry.TABLE_NAME,
            null,
            this.createContactsCV(name, phoneNumber, photo, refTopicId)
        )
    }

    fun updateContacts(
        contactsId: Long,
        name: String?,
        phoneNumber: String?,
        photo: String?
    ): Long {
        val values = ContentValues()
        values.put(ContactsEntry.COLUMN_NAME, name)
        values.put(ContactsEntry.COLUMN_PHONENUMBER, phoneNumber)
        values.put(ContactsEntry.COLUMN_PHOTO, photo)

        return db!!.update(
            ContactsEntry.TABLE_NAME,
            values,
            BaseColumns._ID + " = ?",
            arrayOf<String>(contactsId.toString())
        ).toLong()
    }


    fun delContacts(contactsId: Long): Long {
        return db!!.delete(
            ContactsEntry.TABLE_NAME,
            BaseColumns._ID + " = ?",
            arrayOf<String>(contactsId.toString())
        ).toLong()
    }

    fun delAllContactsInTopic(topicId: Long): Long {
        return db!!.delete(
            ContactsEntry.TABLE_NAME,
            ContactsEntry.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString())
        ).toLong()
    }

    fun selectContacts(topicId: Long): Cursor {
        val c = db!!.query(
            ContactsEntry.TABLE_NAME,
            null,
            ContactsEntry.COLUMN_REF_TOPIC__ID + " = ?",
            arrayOf<String>(topicId.toString()),
            null,
            null,
            BaseColumns._ID + " DESC",
            null
        )
        if (c != null) {
            c.moveToFirst()
        }
        return c
    }

    private fun createContactsCV(
        name: String?,
        phoneNumber: String?,
        photo: String?,
        refTopicId: Long
    ): ContentValues {
        val values = ContentValues()
        values.put(ContactsEntry.COLUMN_NAME, name)
        values.put(ContactsEntry.COLUMN_PHONENUMBER, phoneNumber)
        values.put(ContactsEntry.COLUMN_PHOTO, photo)
        values.put(ContactsEntry.COLUMN_REF_TOPIC__ID, refTopicId)
        return values
    }

    /**
     * For version 4 onUpgrade
     */
    fun selectAllV1Diary(): Cursor {
        val c = db!!.query(
            DBStructure.DiaryEntry.TABLE_NAME, null, null, null,
            null, null, null, null
        )
        c.moveToFirst()
        return c
    }

    /*
     * Debug
     */
    //For Debug
    fun showCursor(cursor: Cursor) {
        for (i in 0..<cursor.count) {
            val sb = StringBuilder()
            val columnsQty = cursor.columnCount
            for (idx in 0..<columnsQty) {
                sb.append(" ").append(idx).append(" = ")
                sb.append(cursor.getString(idx))
                if (idx < columnsQty - 1) sb.append(" ; ")
            }
            Log.e("test", String.format("Row: %d, Values: %s", cursor.position, sb.toString()))
            cursor.moveToNext()
        }
        //Revert Cursor
        cursor.moveToFirst()
    }
}
