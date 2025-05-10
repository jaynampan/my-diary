package meow.softer.mydiary.backup

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.nononsenseapps.filepicker.FilePickerFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.ThemeManager.Companion.instance
import java.io.File

class DirectoryPickerFragment : FilePickerFragment() {
    private var mRecyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_filepicker, container, false)
        //Set toolbar
        val toolbar = rootView.findViewById<Toolbar>(R.id.my_picker_toolbar)
        toolbar.setBackgroundColor(instance!!.getThemeMainColor(requireActivity()))
        //set RecyclerView
        mRecyclerView = rootView.findViewById<RecyclerView>(R.id.RV_filepicker)
        mRecyclerView!!.setBackgroundColor(Color.WHITE)

        //set Button
        (rootView.findViewById<View?>(R.id.my_button_cancel) as Button).text = resources.getString(R.string.dialog_button_cancel)
        (rootView.findViewById<View?>(R.id.my_button_ok) as Button).text = resources.getString(R.string.dialog_button_ok)

        return rootView
    }

    val backTop: File
        /**
         * For consistency, the top level the back button checks against should be the start path.
         * But it will fall back on /.
         */
        get() = getPath(requireArguments().getString(KEY_START_PATH, "/"))

    /**
     * @return true if the current path is the startpath or /
     */
    fun isBackTop(): Boolean {
        return 0 == compareFiles(mCurrentPath, this.backTop) ||
                0 == compareFiles(mCurrentPath, File("/"))
    }

    /**
     * Go up on level, same as pressing on "..".
     */
    override fun goUp() {
        mCurrentPath = getParent(mCurrentPath)
        mCheckedItems.clear()
        mCheckedVisibleViewHolders.clear()
        refresh(mCurrentPath)
    }
}
