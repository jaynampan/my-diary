package meow.softer.mydiary.backup;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.ThemeManager;
import com.nononsenseapps.filepicker.FilePickerFragment;

import java.io.File;

public class DirectoryPickerFragment extends FilePickerFragment {
    private RecyclerView mRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filepicker,container,false);
        //Set toolbar
        Toolbar toolbar = rootView.findViewById(R.id.my_picker_toolbar);
        toolbar.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
        //set RecyclerView
        mRecyclerView=rootView.findViewById(R.id.RV_filepicker);
        mRecyclerView.setBackgroundColor(Color.WHITE);

        //set Button
        ((Button) rootView.findViewById(R.id.my_button_cancel))
                .setText(getResources().getString(R.string.dialog_button_cancel));
        ((Button) rootView.findViewById(R.id.my_button_ok))
                .setText(getResources().getString(R.string.dialog_button_ok));

        return rootView;
    }
    /**
     * For consistency, the top level the back button checks against should be the start path.
     * But it will fall back on /.
     */
    public File getBackTop() {
        return getPath(getArguments().getString(KEY_START_PATH, "/"));
    }

    /**
     * @return true if the current path is the startpath or /
     */
    public boolean isBackTop() {
        return 0 == compareFiles(mCurrentPath, getBackTop()) ||
                0 == compareFiles(mCurrentPath, new File("/"));
    }

    /**
     * Go up on level, same as pressing on "..".
     */
    public void goUp() {
        mCurrentPath = getParent(mCurrentPath);
        mCheckedItems.clear();
        mCheckedVisibleViewHolders.clear();
        refresh(mCurrentPath);
    }
}
