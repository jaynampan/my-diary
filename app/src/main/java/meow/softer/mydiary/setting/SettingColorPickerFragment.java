package meow.softer.mydiary.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import meow.softer.mydiary.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

public class SettingColorPickerFragment extends DialogFragment implements View.OnClickListener {
    public interface colorPickerCallback {
        void onColorChange(int colorCode, int viewId);
    }

    private int oldColor;
    private int viewId;

    private ColorPicker picker;
    private SVBar svBar;
    private Button But_setting_change_color, But_setting_cancel;

    private colorPickerCallback callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (colorPickerCallback) context;
        } catch (ClassCastException e) {
        }
    }
    public static SettingColorPickerFragment newInstance(int oldColor, int viewId) {
        Bundle args = new Bundle();
        SettingColorPickerFragment fragment = new SettingColorPickerFragment();
        args.putInt("oldColor", oldColor);
        args.putInt("viewId", viewId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog= super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        oldColor = getArguments().getInt("oldColor", 0);
        viewId = getArguments().getInt("viewId", View.NO_ID);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        if (viewId == View.NO_ID) {
            dismiss();
        }
        View rootView = inflater.inflate(R.layout.dialog_fragment_color_picker, container);
        picker = rootView.findViewById(R.id.picker);
        svBar = rootView.findViewById(R.id.svbar);
        But_setting_change_color = rootView.findViewById(R.id.But_setting_change_color);
        But_setting_cancel = rootView.findViewById(R.id.But_setting_cancel);

        picker.addSVBar(svBar);
        picker.setOldCenterColor(oldColor);

        But_setting_change_color.setOnClickListener(this);
        But_setting_cancel.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_setting_change_color:
                callback.onColorChange(picker.getColor(), viewId);
                dismiss();
                break;
            case R.id.But_setting_cancel:
                dismiss();
                break;
        }
    }
}
