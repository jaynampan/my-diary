package meow.softer.mydiary.backup;

import android.os.Environment;

import androidx.annotation.Nullable;

import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;

public class DirectoryPickerActivity extends FilePickerActivity {
    /**
     * Need access to the fragment
     */
    private DirectoryPickerFragment currentFragment;

    /**
     * Return a copy of the new fragment and set the variable above.
     */
    @Override
    protected AbstractFilePickerFragment<File> getFragment(@Nullable String startPath, int mode, boolean allowMultiple, boolean allowCreateDir, boolean allowExistingFile, boolean singleClick) {
        // startPath is allowed to be null.
        // In that case, default folder should be SD-card and not "/"
        String path = (startPath != null ? startPath
                : Environment.getExternalStorageDirectory().getPath());

        currentFragment = new DirectoryPickerFragment();
        currentFragment.setArgs(path, mode, allowMultiple, allowCreateDir,
                allowExistingFile, singleClick);
        return currentFragment;
    }
    /**
     * Override the back-button.
     */
    @Override
    public void onBackPressed() {
        // If at top most level, normal behaviour
        if (currentFragment.isBackTop()) {
            super.onBackPressed();
        } else {
            // Else go up
            currentFragment.goUp();
        }
    }
}
