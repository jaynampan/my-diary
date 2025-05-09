package meow.softer.mydiary.shared.gui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ThemeManager

abstract class CommonDialogFragment : DialogFragment(), View.OnClickListener {
    /**
     * UI
     */
    protected var But_common_ok: MyDiaryButton? = null
    protected var But_common_cancel: MyDiaryButton? = null

    protected var RL_common_view: RelativeLayout? = null
    protected var TV_common_content: TextView? = null

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
        val rootView = inflater.inflate(R.layout.dialog_fragment_common, container)
        RL_common_view = rootView.findViewById<RelativeLayout>(R.id.RL_common_view)

        RL_common_view!!.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )


        TV_common_content = rootView.findViewById<TextView?>(R.id.TV_common_content)
        But_common_ok = rootView.findViewById<MyDiaryButton>(R.id.But_common_ok)
        But_common_cancel = rootView.findViewById<MyDiaryButton>(R.id.But_common_cancel)

        But_common_ok!!.setOnClickListener(this)
        But_common_cancel!!.setOnClickListener(this)
        return rootView
    }

    protected abstract fun okButtonEvent()

    protected abstract fun cancelButtonEvent()

    override fun onClick(v: View) {
        when (v.id) {
            R.id.But_common_ok -> okButtonEvent()
            R.id.But_common_cancel -> cancelButtonEvent()
        }
    }
}
