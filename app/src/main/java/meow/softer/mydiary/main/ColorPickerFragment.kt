package meow.softer.mydiary.main

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.ui.components.DiaryButton
import meow.softer.mydiary.util.debug

class ColorPickerFragment : DialogFragment() {
    interface ColorPickerCallback {
        fun onColorChange(colorCode: Int, viewId: Int)
    }

    private var oldColor = 0
    private var viewId = 0


    private var callback: ColorPickerCallback? = null
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
        dialog?.setCanceledOnTouchOutside(true)
        if (viewId == View.NO_ID) {
            dismiss()
        }
        debug(TAG, "onCreate View")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (callback == null) {
            callback = targetFragment as ColorPickerCallback?
        }
    }

    companion object {
        private const val TAG = "ColorPickerFragment"
        fun newInstance(oldColor: Int, viewId: Int): ColorPickerFragment {
            val args = Bundle()
            val fragment = ColorPickerFragment()
            args.putInt("oldColor", oldColor)
            args.putInt("viewId", viewId)
            fragment.setArguments(args)
            return fragment
        }
    }
}