package meow.softer.mydiary.shared.gui;

import android.app.Dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.ThemeManager;

public abstract class CommonDialogFragment extends DialogFragment implements View.OnClickListener {
    /**
     * UI
     */
    protected MyDiaryButton But_common_ok, But_common_cancel;

    protected RelativeLayout RL_common_view;
    protected TextView TV_common_content;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_common, container);
        RL_common_view = rootView.findViewById(R.id.RL_common_view);

        RL_common_view.setBackgroundColor(
                ThemeManager.getInstance().getThemeMainColor(getActivity()));


        TV_common_content = rootView.findViewById(R.id.TV_common_content);
        But_common_ok = rootView.findViewById(R.id.But_common_ok);
        But_common_cancel = rootView.findViewById(R.id.But_common_cancel);

        But_common_ok.setOnClickListener(this);
        But_common_cancel.setOnClickListener(this);
        return rootView;

    }

    protected abstract void okButtonEvent();

    protected abstract void cancelButtonEvent();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_common_ok:
                okButtonEvent();
                break;
            case R.id.But_common_cancel:
                cancelButtonEvent();
                break;
        }
    }
}
