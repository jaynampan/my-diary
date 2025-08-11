package meow.softer.mydiary.entries.entry

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import meow.softer.mydiary.R
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.entries.diary.item.DiaryItemHelper
import meow.softer.mydiary.entries.diary.item.IDiaryRow
import meow.softer.mydiary.shared.FileManager
import org.apache.commons.io.FileUtils
import java.io.File

class UpdateDiaryTask(
    context: Context, time: Long, title: String?,
    moodPosition: Int, weatherPosition: Int, location: String?,
    attachment: Boolean, diaryItemHelper: DiaryItemHelper,
    fileManager: FileManager, callBack: UpdateDiaryCallBack
) : AsyncTask<Long?, Void, Int>() {
    interface UpdateDiaryCallBack {
        fun onDiaryUpdated()
    }

    private val mContext: Context
    private val dbManager: DBManager = DBManager(context)
    private val time: Long
    private val title: String?
    private val moodPosition: Int
    private val weatherPosition: Int
    private val location: String?
    private val attachment: Boolean
    private val diaryItemHelper: DiaryItemHelper
    private val editCrashFileManager: FileManager
    private var diaryFileManager: FileManager? = null
    private val progressDialog: ProgressDialog

    private val callBack: UpdateDiaryCallBack

    init {
        this.mContext = context
        this.time = time
        this.title = title
        this.moodPosition = moodPosition
        this.location = location
        this.weatherPosition = weatherPosition
        this.attachment = attachment
        this.diaryItemHelper = diaryItemHelper
        this.editCrashFileManager = fileManager
        this.callBack = callBack

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(context.getString(R.string.process_dialog_saving))
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar)
        progressDialog.show()
    }

    override fun doInBackground(vararg params: Long?): Int {
        var updateResult: Int = RESULT_UPDATE_SUCCESSFUL
        val topicId: Long = params[0]!!
        val diaryId: Long = params[1]!!
        //This don't use transaction because the file was deleted ,
        // so make it insert some update and show toast.
        try {
            dbManager.openDB()
            //Delete all item first
            dbManager.delAllDiaryItemByDiaryId(diaryId)
            //Delete old photo
            diaryFileManager = FileManager(mContext, topicId, diaryId)
            diaryFileManager!!.clearDir()
            //Update Diary
            dbManager.updateDiary(
                diaryId,
                time,
                title,
                moodPosition,
                weatherPosition,
                location,
                attachment
            )
            for (i in 0..<diaryItemHelper.itemSize) {
                //Copy photo from temp to diary dir
                if (diaryItemHelper.get(i)!!.type == IDiaryRow.TYPE_PHOTO) {
                    savePhoto(diaryItemHelper.get(i)!!.content)
                }
                //Save new data item
                dbManager.insertDiaryContent(
                    diaryItemHelper.get(i)!!.type, i,
                    diaryItemHelper.get(i)!!.content, diaryId
                )
            }
            //Delete all dir if it is no file.
            if (diaryFileManager!!.dir!!.listFiles().size == 0) {
                FileUtils.deleteDirectory(diaryFileManager!!.dir)
            }
        } catch (e: Exception) {
            updateResult = RESULT_UPDATE_ERROR
        } finally {
            dbManager.closeDB()
            editCrashFileManager.clearDir()
        }
        return updateResult
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        progressDialog.dismiss()
        if (result == RESULT_UPDATE_SUCCESSFUL) {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_diary_update_successful),
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_diary_update_fail),
                Toast.LENGTH_LONG
            ).show()
        }
        callBack.onDiaryUpdated()
    }

    @Throws(Exception::class)
    private fun savePhoto(filename: String?) {
        FileManager.copy(
            File(editCrashFileManager.dirAbsolutePath + "/" + filename),
            File(diaryFileManager!!.dirAbsolutePath + "/" + filename)
        )
    }

    companion object {
        const val RESULT_UPDATE_SUCCESSFUL: Int = 1
        const val RESULT_UPDATE_ERROR: Int = 2
    }
}
