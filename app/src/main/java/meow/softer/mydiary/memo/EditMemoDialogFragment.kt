package meow.softer.mydiary.memo

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.data.local.db.DBManager
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.gui.MyDiaryButton

class EditMemoDialogFragment : DialogFragment(), View.OnClickListener {
    /**
     * Callback
     */
    interface MemoCallback {
        fun addMemo(memoContent: String?)

        fun updateMemo()
    }

    private var callback: MemoCallback? = null

    /**
     * UI
     */
    private var But_edit_memo_ok: MyDiaryButton? = null
    private var But_edit_memo_cancel: MyDiaryButton? = null
    private var EDT_edit_memo_content: EditText? = null

    /**
     * Info
     */
    private var topicId: Long = -1

    //default = -1 , it means add memo.
    private var memoId: Long = -1
    private var isAdd = true
    private var memoContent: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as MemoCallback
        } catch (_: ClassCastException) {
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(false)
        val rootView = inflater.inflate(R.layout.dialog_fragment_edit_memo, container)
        EDT_edit_memo_content = rootView.findViewById<EditText>(R.id.EDT_edit_memo_content)
        But_edit_memo_ok = rootView.findViewById<MyDiaryButton>(R.id.But_edit_memo_ok)
        But_edit_memo_cancel = rootView.findViewById<MyDiaryButton>(R.id.But_edit_memo_cancel)

        EDT_edit_memo_content!!.background.mutate().setColorFilter(
            ThemeManager.instance!!.getThemeMainColor(requireContext()), PorterDuff.Mode.SRC_ATOP
        )
        EDT_edit_memo_content!!.setTextColor(
            ThemeManager.instance!!.getThemeDarkColor(requireContext())
        )
        But_edit_memo_ok!!.setOnClickListener(this@EditMemoDialogFragment)
        But_edit_memo_cancel!!.setOnClickListener(this@EditMemoDialogFragment)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicId = requireArguments().getLong("topicId", -1L)
        memoId = requireArguments().getLong("memoId", -1L)
        isAdd = requireArguments().getBoolean("isAdd", true)
        memoContent = requireArguments().getString("memoContent", "")
        EDT_edit_memo_content!!.setText(memoContent)
        //For show keyboard
        EDT_edit_memo_content!!.requestFocus()
        dialog!!.window?.setSoftInputMode(
        (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE))
    }

    private fun addMemo() {
        if (topicId != -1L) {
            callback!!.addMemo(EDT_edit_memo_content!!.getText().toString())
        }
    }

    private fun updateMemo() {
        if (memoId != -1L) {
            val dbManager = DBManager(activity)
            dbManager.openDB()
            dbManager.updateMemoContent(memoId, EDT_edit_memo_content!!.getText().toString())
            dbManager.closeDB()
        }
    }

    private fun okButtonEvent() {
        if (EDT_edit_memo_content!!.getText().toString().isNotEmpty()) {
            if (isAdd) {
                addMemo()
            } else {
                updateMemo()
                callback!!.updateMemo()
            }
            dismiss()
        } else {
            Toast.makeText(activity, getString(R.string.toast_memo_empty), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.But_edit_memo_ok -> okButtonEvent()
            R.id.But_edit_memo_cancel -> dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            topicId: Long,
            memoId: Long,
            isAdd: Boolean,
            memoContent: String?
        ): EditMemoDialogFragment {
            val args = Bundle()
            val fragment = EditMemoDialogFragment()
            args.putLong("topicId", topicId)
            args.putLong("memoId", memoId)
            args.putBoolean("isAdd", isAdd)
            args.putString("memoContent", memoContent)
            fragment.setArguments(args)
            return fragment
        }
    }
}
