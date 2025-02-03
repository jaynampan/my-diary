package meow.softer.mydiary.entries.diary;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.gui.CommonDialogFragment;

public class ClearDialogFragment extends CommonDialogFragment {
    /**
     * Callback
     */
    public interface ClearDialogCallback {
        void onClear();
    }

    private ClearDialogCallback callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            callback = (ClearDialogCallback) getTargetFragment();
//            Activity activity = getActivity();
//            if (activity instanceof ClearDialogCallback) {
//                callback = (ClearDialogCallback) activity;
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        super.onViewCreated(view, savedInstanceState);
        this.TV_common_content.setText(getString(R.string.diary_clear_message));

    }

    @Override
    protected void okButtonEvent() {
        callback.onClear();
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        dismiss();
    }
}
