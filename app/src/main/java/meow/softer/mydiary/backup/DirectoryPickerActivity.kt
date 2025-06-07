package meow.softer.mydiary.backup

import android.os.Environment
import com.nononsenseapps.filepicker.AbstractFilePickerFragment
import com.nononsenseapps.filepicker.FilePickerActivity
import java.io.File

class DirectoryPickerActivity : FilePickerActivity() {
    /**
     * Need access to the fragment
     */
    private var currentFragment: DirectoryPickerFragment? = null

    /**
     * Return a copy of the new fragment and set the variable above.
     */
    override fun getFragment(
        startPath: String?,
        mode: Int,
        allowMultiple: Boolean,
        allowCreateDir: Boolean,
        allowExistingFile: Boolean,
        singleClick: Boolean
    ): AbstractFilePickerFragment<File?> {
        // startPath is allowed to be null.
        // In that case, default folder should be SD-card and not "/"
        val path = (startPath ?: Environment.getExternalStorageDirectory().path)

        currentFragment = DirectoryPickerFragment()
        currentFragment!!.setArgs(
            path, mode, allowMultiple, allowCreateDir,
            allowExistingFile, singleClick
        )
        return currentFragment!!
    }

    /**
     * Override the back-button.
     */
    override fun onBackPressed() {
        // If at top most level, normal behaviour
        if (currentFragment!!.isBackTop()) {
            super.onBackPressed()
        } else {
            // Else go up
            currentFragment!!.goUp()
        }
    }
}
