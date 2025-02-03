package meow.softer.mydiary.main;

import android.app.Dialog;
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

public class ColorPickerFragment extends DialogFragment implements View.OnClickListener {
    public interface colorPickerCallback {
        void onColorChange(int colorCode, int viewId);
    }

    private int oldColor;
    private int viewId;

    private ColorPicker picker;
    private SVBar svBar;
    private Button But_setting_change_color, But_setting_cancel;

    private colorPickerCallback callback;
    public static ColorPickerFragment newInstance(int oldColor, int viewId) {
        Bundle args = new Bundle();
        ColorPickerFragment fragment = new ColorPickerFragment();
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
        picker = (ColorPicker) rootView.findViewById(R.id.picker);
        svBar = (SVBar) rootView.findViewById(R.id.svbar);
        But_setting_change_color = (Button) rootView.findViewById(R.id.But_setting_change_color);
        But_setting_cancel = (Button) rootView.findViewById(R.id.But_setting_cancel);

        picker.addSVBar(svBar);
        picker.setOldCenterColor(oldColor);

        But_setting_change_color.setOnClickListener(this);
        But_setting_cancel.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (callback == null) {
            callback = (colorPickerCallback) getTargetFragment();
        }
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
