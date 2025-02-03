package meow.softer.mydiary.entries;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.gui.CommonDialogFragment;

public class EditDiaryBackDialogFragment extends CommonDialogFragment {
    /**
     * Callback
     */
    public interface BackDialogCallback {
        void onBack();
    }

    private BackDialogCallback callback;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callback = (BackDialogCallback) getTargetFragment();
        this.getDialog().setCanceledOnTouchOutside(true);
        super.onViewCreated(view, savedInstanceState);
        this.TV_common_content.setText(getString(R.string.diary_back_message));
    }

    @Override
    protected void okButtonEvent() {
        callback.onBack();
        dismiss();
    }

    @Override
    protected void cancelButtonEvent() {
        dismiss();
    }
}
