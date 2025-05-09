package meow.softer.mydiary.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CheckedTextView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.gui.MyDiaryButton

class ReleaseNoteDialogFragment : DialogFragment(), View.OnClickListener {
    /**
     * UI
     */
    private var RL_release_note: RelativeLayout? = null
    private var TV_release_note_text: TextView? = null
    private var CTV_release_note_knew: CheckedTextView? = null
    private var But_release_note_ok: MyDiaryButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val rootView = inflater.inflate(R.layout.dialog_fragment_release_note, container)

        RL_release_note = rootView.findViewById<RelativeLayout?>(R.id.RL_release_note)
        RL_release_note!!.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )

        TV_release_note_text = rootView.findViewById<TextView?>(R.id.TV_release_note_text)
        TV_release_note_text!!.text = getString(R.string.release_note)

        CTV_release_note_knew = rootView.findViewById<CheckedTextView?>(R.id.CTV_release_note_knew)
        CTV_release_note_knew!!.setOnClickListener(this)

        But_release_note_ok = rootView.findViewById<MyDiaryButton?>(R.id.But_release_note_ok)
        But_release_note_ok!!.setOnClickListener(this)
        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.CTV_release_note_knew -> CTV_release_note_knew!!.toggle()
            R.id.But_release_note_ok -> {
                SPFManager.setReleaseNoteClose(requireContext(), !CTV_release_note_knew!!.isChecked)
                dismiss()
            }
        }
    }
}