package meow.softer.mydiary.entries.diary

import android.app.Dialog
import android.os.Bundle
import android.view.View
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.gui.CommonDialogFragment

class ClearDialogFragment : CommonDialogFragment() {
    /**
     * Callback
     */
    interface ClearDialogCallback {
        fun onClear()
    }

    private var callback: ClearDialogCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        try {
            callback = targetFragment as ClearDialogCallback?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.dialog!!.setCanceledOnTouchOutside(true)
        super.onViewCreated(view, savedInstanceState)
        this.TV_common_content?.text = getString(R.string.diary_clear_message)
    }

    override fun okButtonEvent() {
        callback!!.onClear()
        dismiss()
    }

    override fun cancelButtonEvent() {
        dismiss()
    }
}
