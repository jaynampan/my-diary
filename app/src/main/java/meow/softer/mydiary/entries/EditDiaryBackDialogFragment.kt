package meow.softer.mydiary.entries

import android.os.Bundle
import android.view.View
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.gui.CommonDialogFragment

class EditDiaryBackDialogFragment : CommonDialogFragment() {
    /**
     * Callback
     */
    interface BackDialogCallback {
        fun onBack()
    }

    private var callback: BackDialogCallback? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback = targetFragment as BackDialogCallback?
        this.dialog!!.setCanceledOnTouchOutside(true)
        super.onViewCreated(view, savedInstanceState)
        this.content = getString(R.string.diary_back_message)
    }

    override fun okButtonEvent() {
        callback!!.onBack()
        dismiss()
    }

    override fun cancelButtonEvent() {
        dismiss()
    }
}
