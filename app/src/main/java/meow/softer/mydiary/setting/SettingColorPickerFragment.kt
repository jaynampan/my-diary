package meow.softer.mydiary.setting

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.larswerkman.holocolorpicker.ColorPicker
import com.larswerkman.holocolorpicker.SVBar
import meow.softer.mydiary.R
import meow.softer.mydiary.main.ColorPicker
import meow.softer.mydiary.main.ColorPickerDialog
import meow.softer.mydiary.ui.components.DiaryButton

class SettingColorPickerFragment : DialogFragment() {
    interface colorPickerCallback {
        fun onColorChange(colorCode: Int, viewId: Int)
    }

    private var oldColor = 0
    private var viewId = 0


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
    ): View {
        this.dialog?.setCanceledOnTouchOutside(true)
        if (viewId == View.NO_ID) {
            dismiss()
        }
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ColorPickerDialog(
                    onConfirm = {
                        callback!!.onColorChange(Color.HSVToColor(it), viewId = viewId)
                        dismiss()
                    },
                    onCancel = {
                        dismiss()
                    }
                )
            }
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

