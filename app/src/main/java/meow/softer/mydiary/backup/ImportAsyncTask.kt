package meow.softer.mydiary.backup

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import meow.softer.mydiary.R
import meow.softer.mydiary.backup.BackupManager.BackupTopicListBean
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.shared.FileManager
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

class ImportAsyncTask(context: Context, callBack: ImportCallBack, backupZieFilePath: String?) :
    AsyncTask<Void?, Void?, Boolean>() {
    interface ImportCallBack {
        fun onImportCompiled(importSuccessful: Boolean)
    }

    private var backupManager: BackupManager? = null
    private val backupFileManager: FileManager
    private val diaryFileManager: FileManager
    private val backupJsonFilePath: String?
    private val backupZieFilePath: String?

    private val dbManager: DBManager = DBManager(context)

    private val mContext: Context = context
    private val progressDialog: ProgressDialog
    private val callBack: ImportCallBack

    init {
        val backFM = FileManager(context, FileManager.BACKUP_DIR)
        this.backupJsonFilePath = (backFM.dirAbsolutePath + "/"
                + BackupManager.BACKUP_JSON_FILE_NAME)
        this.backupZieFilePath = backupZieFilePath

        this.backupFileManager = FileManager(mContext, FileManager.BACKUP_DIR)
        this.diaryFileManager = FileManager(mContext, FileManager.DIARY_ROOT_DIR)

        this.callBack = callBack
        this.progressDialog = ProgressDialog(context)

        //Init progressDialog
        progressDialog.setMessage(context.getString(R.string.process_dialog_loading))
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar)
        progressDialog.show()
    }

    override fun doInBackground(vararg voids: Void?): Boolean {
        var importSuccessful = true
        try {
            val zipManager = ZipManager(mContext)

            val zipBackupFM = FileManager(mContext, FileManager.BACKUP_DIR)
            zipManager.unzip(
                backupZieFilePath,
                zipBackupFM.dirAbsolutePath + "/"
            )
            loadBackupJsonFileIntoManager()
            importSuccessful = importTopic()
        } catch (e: Exception) {
            Log.e(TAG, "import flow fail", e)
            importSuccessful = false
        } finally {
            backupFileManager.clearDir()
        }
        return importSuccessful
    }

    override fun onPostExecute(importSuccessful: Boolean) {
        super.onPostExecute(importSuccessful)
        progressDialog.dismiss()
        if (importSuccessful) {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_import_successful),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_import_fail),
                Toast.LENGTH_LONG
            ).show()
        }
        callBack.onImportCompiled(importSuccessful)
    }

    @Throws(Exception::class)
    private fun loadBackupJsonFileIntoManager() {
        val fis = FileInputStream(backupJsonFilePath)
        val isr = InputStreamReader(fis)
        val bufferedReader = BufferedReader(isr)
        val sb = StringBuilder()
        var line: String?
        while ((bufferedReader.readLine().also { line = it }) != null) {
            sb.append(line)
        }
        val json = sb.toString()
        val gson = Gson()
        backupManager = gson.fromJson<BackupManager>(json, BackupManager::class.java)
        if (backupManager!!.header != BackupManager.header) {
            throw Exception("This is not mydiary backup file")
        }
    }

    private fun importTopic(): Boolean {
        var importSuccessful = true
        try {
            dbManager.openDB()
            //Start a transaction
            dbManager.beginTransaction()
            for (i in backupManager!!.getBackup_topic_list().indices) {
                saveTopicIntoDB(backupManager!!.getBackup_topic_list()[i]!!)
            }

            //Check update success
            dbManager.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "importTopic fail", e)
            importSuccessful = false
        } finally {
            dbManager.endTransaction()
            dbManager.closeDB()
        }
        return importSuccessful
    }

    @Throws(IOException::class)
    private fun saveTopicIntoDB(backupTopic: BackupTopicListBean) {
        val newTopicId = dbManager.insertTopic(
            backupTopic.topic_title,
            backupTopic.topic_type,
            backupTopic.topic_color
        )

        when (backupTopic.topic_type) {
            ITopic.TYPE_MEMO -> if (backupTopic.memo_topic_entries_list != null) {
                var x = 0
                while (x < backupTopic.memo_topic_entries_list!!.size) {
                    val memo = backupTopic.memo_topic_entries_list!![x]
                    val newMemoId = dbManager.insertMemo(
                        memo!!.memoEntriesContent,
                        memo.isChecked,
                        newTopicId
                    )
                    dbManager.insertMemoOrder(
                        newTopicId,
                        newMemoId,
                        memo.memoEntriesOrder.toLong()
                    )
                    x++
                }
            }

            ITopic.TYPE_DIARY -> if (backupTopic.diary_topic_entries_list != null) {
                var y = 0
                while (y < backupTopic.diary_topic_entries_list!!.size) {
                    val diary = backupTopic.diary_topic_entries_list!![y]
                    //Write the diary entries
                    val newDiaryId =
                        dbManager.insertDiaryInfo(
                            diary!!.diaryEntriesTime,
                            diary.diaryEntriesTitle,
                            diary.diaryEntriesMood,
                            diary.diaryEntriesWeather,
                            diary.isDiaryEntriesAttachment,
                            newTopicId,
                            diary.diaryEntriesLocation
                        )
                    //Write the diary item
                    var yi = 0
                    while (yi < diary.diaryItemList!!.size) {
                        val diaryItem = diary.diaryItemList[yi]
                        dbManager.insertDiaryContent(
                            diaryItem!!.diaryItemType,
                            diaryItem.diaryItemPosition,
                            diaryItem.diaryItemContent, newDiaryId
                        )
                        yi++
                    }
                    //Copy the diary photo
                    copyDiaryPhoto(
                        backupTopic.topic_id, newTopicId,
                        diary.diaryEntriesId, newDiaryId
                    )
                    y++
                }
            }

            ITopic.TYPE_CONTACTS -> if (backupTopic.contacts_topic_entries_list != null) {
                var z = 0
                while (z < backupTopic.contacts_topic_entries_list!!.size) {
                    val contact = backupTopic.contacts_topic_entries_list!![z]
                    dbManager.insertContacts(
                        contact!!.contactsEntriesName,
                        contact.contactsEntriesPhoneNumber, "", newTopicId
                    )
                    z++
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyDiaryPhoto(
        oldTopicId: Long, newTopicId: Long,
        oldDiaryId: Long, newDiaryId: Long
    ) {
        val backupDiaryDir = File(
            backupFileManager.dirAbsolutePath + "/diary/" +
                    oldTopicId + "/" + oldDiaryId + "/"
        )
        if (backupDiaryDir.exists() || backupDiaryDir.isDirectory()) {
            val newDiaryDir = File(
                diaryFileManager.dirAbsolutePath + "/" +
                        newTopicId + "/" + newDiaryId + "/"
            )
            FileUtils.moveDirectory(backupDiaryDir, newDiaryDir)
        }
    }

    companion object {
        private const val TAG = "ImportAsyncTask"
    }
}