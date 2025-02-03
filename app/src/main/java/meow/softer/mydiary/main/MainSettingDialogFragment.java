package meow.softer.mydiary.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import meow.softer.mydiary.R;
import meow.softer.mydiary.backup.BackupActivity;
import meow.softer.mydiary.security.PasswordActivity;
import meow.softer.mydiary.setting.SettingActivity;
import meow.softer.mydiary.shared.MyDiaryApplication;
import meow.softer.mydiary.shared.ThemeManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MainSettingDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    /**
     * UI
     */

    private RelativeLayout RL_main_setting_dialog;
    private ImageView IV_main_setting_setting_page, IV_main_setting_add_topic,
            IV_main_setting_setting_security, IV_main_setting_backup, IV_main_setting_about;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog= super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.bottom_sheet_main_setting, container);

        RL_main_setting_dialog = (RelativeLayout) rootView.findViewById(R.id.RL_main_setting_dialog);
        RL_main_setting_dialog.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
        IV_main_setting_setting_page = (ImageView) rootView.findViewById(R.id.IV_main_setting_setting_page);
        IV_main_setting_setting_page.setOnClickListener(this);
        IV_main_setting_add_topic = (ImageView) rootView.findViewById(R.id.IV_main_setting_add_topic);
        IV_main_setting_add_topic.setOnClickListener(this);
        IV_main_setting_setting_security = (ImageView) rootView.findViewById(R.id.IV_main_setting_setting_security);
        IV_main_setting_setting_security.setOnClickListener(this);
        IV_main_setting_backup = (ImageView) rootView.findViewById(R.id.IV_main_setting_backup);
        IV_main_setting_backup.setOnClickListener(this);
        IV_main_setting_about = (ImageView) rootView.findViewById(R.id.IV_main_setting_about);
        IV_main_setting_about.setOnClickListener(this);


        if (((MyDiaryApplication) getActivity().getApplication()).isHasPassword()) {
            IV_main_setting_setting_security.setImageResource(R.drawable.ic_enhanced_encryption_white_36dp);
        } else {
            IV_main_setting_setting_security.setImageResource(R.drawable.ic_no_encryption_white_36dp);

        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_main_setting_add_topic:
                TopicDetailDialogFragment createTopicDialogFragment =
                        TopicDetailDialogFragment.newInstance(false, -1, -1, "", -1, Color.BLACK);
                createTopicDialogFragment.show(getFragmentManager(), "createTopicDialogFragment");
                dismiss();
                break;
            case R.id.IV_main_setting_setting_page:
                Intent settingPageIntent = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(settingPageIntent);
                dismiss();
                break;
            case R.id.IV_main_setting_setting_security:
                Intent securityPageIntent = new Intent(getActivity(), PasswordActivity.class);
                if (((MyDiaryApplication) getActivity().getApplication()).isHasPassword()) {
                    securityPageIntent.putExtra("password_mode", PasswordActivity.REMOVE_PASSWORD);
                } else {
                    securityPageIntent.putExtra("password_mode", PasswordActivity.CREATE_PASSWORD);
                }
                getActivity().startActivity(securityPageIntent);
                dismiss();
                break;
            case R.id.IV_main_setting_backup:
                Intent backupIntent = new Intent(getActivity(), BackupActivity.class);
                getActivity().startActivity(backupIntent);
                dismiss();
                break;
            case R.id.IV_main_setting_about:
                Intent aboutPageIntent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(aboutPageIntent);
                dismiss();
                break;
        }
    }
}
