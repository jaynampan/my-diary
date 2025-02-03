package meow.softer.mydiary.main;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.SPFManager;
import meow.softer.mydiary.shared.ThemeManager;
import meow.softer.mydiary.shared.gui.MyDiaryButton;

public class ReleaseNoteDialogFragment extends DialogFragment implements View.OnClickListener {
    /**
     * UI
     */
    private RelativeLayout RL_release_note;
    private TextView TV_release_note_text;
    private CheckedTextView CTV_release_note_knew;
    private MyDiaryButton But_release_note_ok;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog= super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(false);
        View rootView = inflater.inflate(R.layout.dialog_fragment_release_note, container);

        RL_release_note = rootView.findViewById(R.id.RL_release_note);
        RL_release_note.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        TV_release_note_text = rootView.findViewById(R.id.TV_release_note_text);
        TV_release_note_text.setText(getString(R.string.release_note));

        CTV_release_note_knew = rootView.findViewById(R.id.CTV_release_note_knew);
        CTV_release_note_knew.setOnClickListener(this);

        But_release_note_ok = rootView.findViewById(R.id.But_release_note_ok);
        But_release_note_ok.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTV_release_note_knew:
                CTV_release_note_knew.toggle();
                break;
            case R.id.But_release_note_ok:
                SPFManager.setReleaseNoteClose(getActivity(),!CTV_release_note_knew.isChecked());
                dismiss();
                break;
        }
    }
}
