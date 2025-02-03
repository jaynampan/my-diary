package meow.softer.mydiary.entries.entry;

import static org.apache.commons.io.FileUtils.deleteDirectory;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import meow.softer.mydiary.R;
import meow.softer.mydiary.db.DBManager;
import meow.softer.mydiary.shared.FileManager;
import meow.softer.mydiary.shared.gui.CommonDialogFragment;

import java.io.IOException;

public class DiaryDeleteDialogFragment extends CommonDialogFragment {
    private DeleteCallback callback;


    /**
     * Callback
     */
    public interface DeleteCallback {
        void onDiaryDelete();
    }

    private long diaryId;
    private long topicId;

    public static DiaryDeleteDialogFragment newInstance(long topicId, long diaryId) {
        Bundle args = new Bundle();
        DiaryDeleteDialogFragment fragment = new DiaryDeleteDialogFragment();
        args.putLong("topicId", topicId);
        args.putLong("diaryId", diaryId);
        fragment.setArguments(args);
        return fragment;
    }

    private void deleteDiary() {
        //Delete the db
        DBManager dbManager = new DBManager(getActivity());
        dbManager.openDB();
        dbManager.delDiary(diaryId);
        dbManager.closeDB();
        //Delete photo data
        try {
            deleteDirectory(new FileManager(getActivity(), topicId, diaryId).getDir());
        } catch (IOException e) {
            //just do nothing
            e.printStackTrace();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callback = (DeleteCallback) getTargetFragment();
        this.getDialog().setCanceledOnTouchOutside(false);
        super.onViewCreated(view, savedInstanceState);
        topicId = getArguments().getLong("topicId", -1L);
        diaryId = getArguments().getLong("diaryId", -1L);
        this.TV_common_content.setText(getString(R.string.entries_edit_dialog_delete_content));
    }

    @Override
    protected void okButtonEvent() {
        if (diaryId != -1) {
            deleteDiary();
            this.callback.onDiaryDelete();
        }
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        dismiss();
    }
}
