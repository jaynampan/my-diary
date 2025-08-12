package meow.softer.mydiary.entries.diary

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import meow.softer.mydiary.R
import meow.softer.mydiary.data.local.db.DBManager
import meow.softer.mydiary.entries.diary.item.DiaryItemHelper
import meow.softer.mydiary.entries.diary.item.IDiaryRow
import meow.softer.mydiary.shared.FileManager
import java.io.File

class SaveDiaryTask(
    context: Context, time: Long, title: String?,
    moodPosition: Int, weatherPosition: Int,
    attachment: Boolean, locationName: String?,
    diaryItemHelper: DiaryItemHelper, topicId: Long, callBack: SaveDiaryCallBack
) : AsyncTask<Long?, Void?, Int?>() {
    interface SaveDiaryCallBack {
        fun onDiarySaved()
    }

    private val mContext: Context
    private val dbManager: DBManager
    private val time: Long
    private val title: String?
    private val moodPosition: Int
    private val weatherPosition: Int
    private val attachment: Boolean
    private val locationName: String?
    private val diaryItemHelper: DiaryItemHelper
    private val tempFileManager: FileManager?
    private var diaryFileManager: FileManager? = null
    private val progressDialog: ProgressDialog = ProgressDialog(context)

    private val callBack: SaveDiaryCallBack

    init {
        progressDialog.setMessage(context.getString(R.string.process_dialog_saving))
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar)

        this.dbManager = DBManager(context)
        this.mContext = context
        this.time = time
        this.title = title
        this.moodPosition = moodPosition
        this.weatherPosition = weatherPosition
        this.attachment = attachment
        this.locationName = locationName
        this.diaryItemHelper = diaryItemHelper
        this.tempFileManager = FileManager(context, topicId)
        this.callBack = callBack

        progressDialog.show()
    }

    override fun doInBackground(vararg longs: Long?): Int {
        var saveResult: Int = RESULT_INSERT_SUCCESSFUL
        val topicId: Long = longs[0]!!
        try {
            dbManager.openDB()
            dbManager.beginTransaction()
            //Save info
            val diaryId = dbManager.insertDiaryInfo(
                time,
                title, moodPosition, weatherPosition,
                attachment, topicId, locationName
            )
            //Save content
            diaryFileManager = FileManager(mContext, topicId, diaryId)
            //Check no any garbage in this diary.
            diaryFileManager!!.clearDir()
            if (diaryId != -1L) {
                for (i in 0..<diaryItemHelper.itemSize) {
                    //Copy photo from temp to diary dir
                    if (diaryItemHelper.get(i)!!.type == IDiaryRow.TYPE_PHOTO) {
                        savePhoto(diaryItemHelper.get(i)!!.content)
                    }
                    //Save data
                    dbManager.insertDiaryContent(
                        diaryItemHelper.get(i)!!.type, i,
                        diaryItemHelper.get(i)!!.content, diaryId
                    )
                }
                dbManager.setTransactionSuccessful()
            } else {
                saveResult = RESULT_INSERT_ERROR
            }
        } catch (e: Exception) {
            Log.e(TAG, "save diary fail", e)
            //Revert the Data
            if (diaryFileManager != null) {
                diaryFileManager!!.clearDir()
            }
            saveResult = RESULT_INSERT_ERROR
        } finally {
            dbManager.endTransaction()
            dbManager.closeDB()
        }
        return saveResult
    }

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        if (result == RESULT_INSERT_SUCCESSFUL) {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_diary_insert_successful),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_diary_insert_fail),
                Toast.LENGTH_LONG
            ).show()
        }
        progressDialog.dismiss()
        callBack.onDiarySaved()
    }

    @Throws(Exception::class)
    private fun savePhoto(filename: String?) {
        FileManager.copy(
            File(tempFileManager!!.dirAbsolutePath + "/" + filename),
            File(diaryFileManager!!.dirAbsolutePath + "/" + filename)
        )
    }

    companion object {
        const val TAG: String = "SaveDiaryTask"
        const val RESULT_INSERT_SUCCESSFUL: Int = 1
        const val RESULT_INSERT_ERROR: Int = 2
    }
}
