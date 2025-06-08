package meow.softer.mydiary.backup

import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import meow.softer.mydiary.R
import meow.softer.mydiary.backup.BackupManager.BackupTopicListBean
import meow.softer.mydiary.backup.obj.BUContactsEntries
import meow.softer.mydiary.backup.obj.BUDiaryEntries
import meow.softer.mydiary.backup.obj.BUDiaryItem
import meow.softer.mydiary.backup.obj.BUMemoEntries
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.shared.FileManager
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date

class ExportAsyncTask(
    private var mContext: Context,
    callBack: ExportCallBack,
    backupZipRootPath: String?
) : AsyncTask<Void?, Void?, Boolean>() {
    interface ExportCallBack {
        fun onExportCompiled(backupZipFilePath: String?)
    }

    private val backupManager: BackupManager
    private val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
    private val backupJsonFilePath: String?
    private val backupZipRootPath: String?
    private val backupZipFileName: String

    private val dbManager: DBManager

    private val progressDialog: ProgressDialog
    private val callBack: ExportCallBack

    init {
        this.mContext = mContext
        this.backupManager = BackupManager()
        this.backupManager.initBackupManagerExportInfo()

        this.dbManager = DBManager(mContext)
        val backupFM = FileManager(
            mContext, FileManager.BACKUP_DIR
        )
        this.backupJsonFilePath = (backupFM.dirAbsolutePath + "/"
                + BackupManager.BACKUP_JSON_FILE_NAME)
        this.backupZipRootPath = backupZipRootPath
        this.backupZipFileName =
            BackupManager.Companion.BACKUP_ZIP_FILE_HEADER + sdf.format(Date()) + BackupManager.Companion.BACKUP_ZIP_FILE_SUB_FILE_NAME
        this.callBack = callBack
        this.progressDialog = ProgressDialog(mContext)
        //Init progressDialog
        progressDialog.setMessage(mContext.getString(R.string.process_dialog_loading))
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar)
        progressDialog.show()
    }

    override fun doInBackground(vararg voids: Void?): Boolean {
        var exportSuccessful = true
        try {
            //Load the data
            exportDataIntoBackupManager()
            //Create backup.json
            outputBackupJson()
            //Zip the json file and photo
            zipBackupFile()
            //Delete the json file
            deleteBackupJsonFile()
        } catch (e: Exception) {
            Log.e(TAG, "export fail", e)
            exportSuccessful = false
        }

        return exportSuccessful
    }

    override fun onPostExecute(exportSuccessful: Boolean) {
        super.onPostExecute(exportSuccessful)
        progressDialog.dismiss()
        if (exportSuccessful) {
            Toast.makeText(
                mContext,
                String.format(
                    mContext.resources.getString(R.string.toast_export_successful),
                    backupZipFileName
                ),
                Toast.LENGTH_LONG
            ).show()
            callBack.onExportCompiled("$backupZipRootPath/$backupZipFileName")
        } else {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_export_fail),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun deleteBackupJsonFile() {
        FileManager(mContext, FileManager.BACKUP_DIR).clearDir()
    }

    @Throws(IOException::class)
    private fun outputBackupJson() {
        val writer: Writer = FileWriter(backupJsonFilePath)
        val gson = GsonBuilder().create()
        gson.toJson(backupManager, writer)
        writer.close()
    }

    @Throws(IOException::class)
    private fun zipBackupFile(): Boolean {
        val zipManager = ZipManager(mContext)
        return zipManager.zipFileAtPath(
            backupJsonFilePath,
            "$backupZipRootPath/$backupZipFileName"
        )
    }

    /**
     * Select the all data from DB.
     * the backupManager will be written into a json file.
     */
    @Throws(Exception::class)
    private fun exportDataIntoBackupManager() {
        dbManager.openDB()
        val topicCursor = dbManager.selectTopic()
        for (i in 0..<topicCursor.count) {
            val exportTopic = loadTopicDataFormDB(topicCursor)
            if (exportTopic != null) {
                backupManager.addTopic(exportTopic)
                topicCursor.moveToNext()
            } else {
                throw Exception("backup type Exception")
            }
        }
        topicCursor.close()
        dbManager.closeDB()
    }


    private fun loadTopicDataFormDB(topicCursor: Cursor): BackupTopicListBean? {
        var topic: BackupTopicListBean? = null
        when (topicCursor.getInt(2)) {
            ITopic.TYPE_MEMO -> {
                //Select memo first
                val memoEntriesCursor = dbManager.selectMemoAndMemoOrder(topicCursor.getLong(0))
                val memoEntriesItemList: MutableList<BUMemoEntries?> = ArrayList<BUMemoEntries?>()
                var j = 0
                while (j < memoEntriesCursor.count) {
                    memoEntriesItemList.add(
                        BUMemoEntries(
                            memoEntriesCursor.getString(2), memoEntriesCursor.getInt(7),
                            memoEntriesCursor.getInt(3) > 0
                        )
                    )
                    memoEntriesCursor.moveToNext()
                    j++
                }
                memoEntriesCursor.close()
                //Create the BUmemo
                topic = BackupTopicListBean(
                    topicCursor.getLong(0), topicCursor.getString(1),
                    topicCursor.getInt(7), topicCursor.getInt(5)
                )
                topic.topic_type = ITopic.TYPE_MEMO
                topic.memo_topic_entries_list = memoEntriesItemList
            }

            ITopic.TYPE_DIARY -> {
                //Select diary entries first
                val diaryEntriesCursor = dbManager.selectDiaryList(topicCursor.getLong(0))
                val diaryEntriesItemList: MutableList<BUDiaryEntries?> =
                    ArrayList<BUDiaryEntries?>()

                var j = 0
                while (j < diaryEntriesCursor.count) {
                    val diaryItemCursor =
                        dbManager.selectDiaryContentByDiaryId(diaryEntriesCursor.getLong(0))
                    val diaryItemItemList: MutableList<BUDiaryItem?> = ArrayList<BUDiaryItem?>()
                    var k = 0
                    while (k < diaryItemCursor.count) {
                        diaryItemItemList.add(
                            BUDiaryItem(
                                diaryItemCursor.getInt(1), diaryItemCursor.getInt(2),
                                diaryItemCursor.getString(3)
                            )
                        )
                        diaryItemCursor.moveToNext()
                        k++
                    }
                    diaryItemCursor.close()
                    diaryEntriesItemList.add(
                        BUDiaryEntries(
                            diaryEntriesCursor.getLong(0),
                            diaryEntriesCursor.getLong(1), diaryEntriesCursor.getString(2),
                            diaryEntriesCursor.getInt(3), diaryEntriesCursor.getInt(4),
                            diaryEntriesCursor.getInt(5) > 0,
                            diaryEntriesCursor.getString(7), diaryItemItemList
                        )
                    )
                    diaryEntriesCursor.moveToNext()
                    j++
                }
                diaryEntriesCursor.close()
                //Create the BU Diary
                topic = BackupTopicListBean(
                    topicCursor.getLong(0), topicCursor.getString(1),
                    topicCursor.getInt(7), topicCursor.getInt(5)
                )
                topic.topic_type = ITopic.TYPE_DIARY
                topic.diary_topic_entries_list = diaryEntriesItemList
            }

            ITopic.TYPE_CONTACTS -> {
                //Select contacts entries first
                val contactsEntriesCursor = dbManager.selectContacts(topicCursor.getLong(0))
                val contactsEntriesItemList: MutableList<BUContactsEntries?> =
                    ArrayList<BUContactsEntries?>()
                var j = 0
                while (j < contactsEntriesCursor.count) {
                    contactsEntriesItemList.add(
                        BUContactsEntries(
                            contactsEntriesCursor.getLong(0),
                            contactsEntriesCursor.getString(1),
                            contactsEntriesCursor.getString(2)
                        )
                    )
                    contactsEntriesCursor.moveToNext()
                    j++
                }
                contactsEntriesCursor.close()
                //Create the BU memo
                topic = BackupTopicListBean(
                    topicCursor.getLong(0), topicCursor.getString(1),
                    topicCursor.getInt(7), topicCursor.getInt(5)
                )
                topic.topic_type = ITopic.TYPE_CONTACTS
                topic.contacts_topic_entries_list = contactsEntriesItemList
            }
        }
        return topic
    }

    companion object {
        private const val TAG = "ExportAsyncTask"
    }
}