package meow.softer.mydiary.memo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import meow.softer.mydiary.R;
import meow.softer.mydiary.db.DBManager;
import meow.softer.mydiary.shared.ThemeManager;
import meow.softer.mydiary.shared.gui.MyDiaryButton;

public class EditMemoDialogFragment extends DialogFragment implements View.OnClickListener {
    /**
     * Callback
     */
    public interface MemoCallback {
        void addMemo(String memoContent);

        void updateMemo();
    }

    private MemoCallback callback;
    /**
     * UI
     */
    private MyDiaryButton But_edit_memo_ok, But_edit_memo_cancel;
    private EditText EDT_edit_memo_content;

    /**
     * Info
     */
    private long topicId = -1;
    //default = -1 , it means add memo.
    private long memoId = -1;
    private boolean isAdd = true;
    private String memoContent = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public static EditMemoDialogFragment newInstance(long topicId, long memoId, boolean isAdd, String memoContent) {
        Bundle args = new Bundle();
        EditMemoDialogFragment fragment = new EditMemoDialogFragment();
        args.putLong("topicId", topicId);
        args.putLong("memoId", memoId);
        args.putBoolean("isAdd", isAdd);
        args.putString("memoContent", memoContent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (MemoCallback) context;
        } catch (ClassCastException e) {
        }
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
        View rootView = inflater.inflate(R.layout.dialog_fragment_edit_memo, container);
        EDT_edit_memo_content = rootView.findViewById(R.id.EDT_edit_memo_content);
        But_edit_memo_ok = rootView.findViewById(R.id.But_edit_memo_ok);
        But_edit_memo_cancel = rootView.findViewById(R.id.But_edit_memo_cancel);

        EDT_edit_memo_content.getBackground().mutate().setColorFilter(
                ThemeManager.getInstance().getThemeMainColor(getActivity()), PorterDuff.Mode.SRC_ATOP);
        EDT_edit_memo_content.setTextColor(ThemeManager.getInstance().getThemeDarkColor(getActivity()));
        But_edit_memo_ok.setOnClickListener(EditMemoDialogFragment.this);
        But_edit_memo_cancel.setOnClickListener(EditMemoDialogFragment.this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topicId = getArguments().getLong("topicId", -1L);
        memoId = getArguments().getLong("memoId", -1L);
        isAdd = getArguments().getBoolean("isAdd", true);
        memoContent = getArguments().getString("memoContent", "");
        EDT_edit_memo_content.setText(memoContent);
        //For show keyboard
        EDT_edit_memo_content.requestFocus();
        getDialog().getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
    private void addMemo() {
        if (topicId != -1) {
            callback.addMemo(EDT_edit_memo_content.getText().toString());
        }
    }

    private void updateMemo() {
        if (memoId != -1) {
            DBManager dbManager = new DBManager(getActivity());
            dbManager.openDB();
            dbManager.updateMemoContent(memoId, EDT_edit_memo_content.getText().toString());
            dbManager.closeDB();
        }
    }

    private void okButtonEvent() {

        if (EDT_edit_memo_content.getText().toString().length() > 0) {
            if (isAdd) {
                addMemo();
            } else {
                updateMemo();
                callback.updateMemo();
            }
            dismiss();
        } else {
            Toast.makeText(getActivity(), getString(R.string.toast_memo_empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.But_edit_memo_ok:
                okButtonEvent();
                break;
            case R.id.But_edit_memo_cancel:
                dismiss();
                break;
        }
    }
}
