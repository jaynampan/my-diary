package meow.softer.mydiary.entries.entry

import android.os.Bundle
import android.view.View
import meow.softer.mydiary.R
import meow.softer.mydiary.data.db.DBManager
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.gui.CommonDialogFragment
import org.apache.commons.io.FileUtils
import java.io.IOException

class DiaryDeleteDialogFragment : CommonDialogFragment() {
    private var callback: DeleteCallback? = null


    /**
     * Callback
     */
    interface DeleteCallback {
        fun onDiaryDelete()
    }

    private var diaryId: Long = 0
    private var topicId: Long = 0

    private fun deleteDiary() {
        //Delete the db
        val dbManager = DBManager(activity)
        dbManager.openDB()
        dbManager.delDiary(diaryId)
        dbManager.closeDB()
        //Delete photo data
        try {
            FileUtils.deleteDirectory(FileManager(requireContext(), topicId, diaryId).dir)
        } catch (e: IOException) {
            //just do nothing
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback = targetFragment as DeleteCallback?
        this.dialog!!.setCanceledOnTouchOutside(false)
        super.onViewCreated(view, savedInstanceState)
        topicId = requireArguments().getLong("topicId", -1L)
        diaryId = requireArguments().getLong("diaryId", -1L)
        this.content = getString(R.string.entries_edit_dialog_delete_content)
    }

    override fun okButtonEvent() {
        if (diaryId != -1L) {
            deleteDiary()
            this.callback!!.onDiaryDelete()
        }
        dismiss()
    }

    override fun cancelButtonEvent() {
        dismiss()
    }

    companion object {
        @JvmStatic
        fun newInstance(topicId: Long, diaryId: Long): DiaryDeleteDialogFragment {
            val args = Bundle()
            val fragment = DiaryDeleteDialogFragment()
            args.putLong("topicId", topicId)
            args.putLong("diaryId", diaryId)
            fragment.setArguments(args)
            return fragment
        }
    }
}
