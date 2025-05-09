package meow.softer.mydiary.entries.entry

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.FileManager
import java.io.File

class CopyDiaryToEditCacheTask(
    private val mContext: Context, private val editCacheFileManage: FileManager,
    private val callBack: EditTaskCallBack
) : AsyncTask<Long?, Void?, Int?>() {
    interface EditTaskCallBack {
        fun onCopyToEditCacheCompiled(result: Int)
    }

    override fun doInBackground(vararg params: Long?): Int {
        var copyResult: Int = RESULT_COPY_SUCCESSFUL
        val topicId: Long = params[0]!!
        val diaryId: Long = params[1]!!
        try {
            val diaryFileManager = FileManager(mContext, topicId, diaryId)
            val childrenPhoto = diaryFileManager.dir.listFiles()
            for (i in diaryFileManager.dir.listFiles().indices) {
                copyPhoto(childrenPhoto!![i]!!.getName(), diaryFileManager)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            copyResult = RESULT_COPY_ERROR
        }
        return copyResult
    }

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)
        if (result == RESULT_COPY_ERROR) {
            Toast.makeText(
                mContext,
                mContext.getString(R.string.toast_diary_copy_to_edit_fail),
                Toast.LENGTH_LONG
            ).show()
        }
        callBack.onCopyToEditCacheCompiled(result!!)
    }

    @Throws(Exception::class)
    private fun copyPhoto(filename: String?, diaryFileManager: FileManager) {
        FileManager.copy(
            File(diaryFileManager.dirAbsolutePath + "/" + filename),
            File(editCacheFileManage.dirAbsolutePath + "/" + filename)
        )
    }

    companion object {
        const val RESULT_COPY_SUCCESSFUL: Int = 1
        const val RESULT_COPY_ERROR: Int = 2
    }
}
