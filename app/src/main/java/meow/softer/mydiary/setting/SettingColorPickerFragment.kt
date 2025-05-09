package meow.softer.mydiary.setting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.SVBar
import meow.softer.mydiary.R

class SettingColorPickerFragment : DialogFragment(), View.OnClickListener {
    interface colorPickerCallback {
        fun onColorChange(colorCode: Int, viewId: Int)
    }

    private var oldColor = 0
    private var viewId = 0

    private var picker: ColorPicker? = null
    private var svBar: SVBar? = null
    private var But_setting_change_color: Button? = null
    private var But_setting_cancel: Button? = null

    private var callback: colorPickerCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as colorPickerCallback
        } catch (e: ClassCastException) {
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        oldColor = requireArguments().getInt("oldColor", 0)
        viewId = requireArguments().getInt("viewId", View.NO_ID)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(true)
        if (viewId == View.NO_ID) {
            dismiss()
        }
        val rootView = inflater.inflate(R.layout.dialog_fragment_color_picker, container)
        picker = rootView.findViewById<ColorPicker>(R.id.picker)
        svBar = rootView.findViewById<SVBar?>(R.id.svbar)
        But_setting_change_color = rootView.findViewById<Button>(R.id.But_setting_change_color)
        But_setting_cancel = rootView.findViewById<Button>(R.id.But_setting_cancel)

        picker!!.addSVBar(svBar)
        picker!!.setOldCenterColor(oldColor)

        But_setting_change_color!!.setOnClickListener(this)
        But_setting_cancel!!.setOnClickListener(this)
        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.But_setting_change_color -> {
                callback!!.onColorChange(picker!!.color, viewId)
                dismiss()
            }

            R.id.But_setting_cancel -> dismiss()
        }
    }

    companion object {
        fun newInstance(oldColor: Int, viewId: Int): SettingColorPickerFragment {
            val args = Bundle()
            val fragment = SettingColorPickerFragment()
            args.putInt("oldColor", oldColor)
            args.putInt("viewId", viewId)
            fragment.setArguments(args)
            return fragment
        }
    }
}
