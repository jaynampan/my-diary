package meow.softer.mydiary.shared.gui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.ui.components.CommonDialog

abstract class CommonDialogFragment : DialogFragment() {
    protected var content :String =""

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
        val composeView = rootView.findViewById<ComposeView>(R.id.compose_view)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                CommonDialog(
                    content = content,
                    onConfirm = { okButtonEvent() },
                    onCancel = {cancelButtonEvent()}
                )
            }
        }
        return rootView
    }

    protected abstract fun okButtonEvent()

    protected abstract fun cancelButtonEvent()
}
