package meow.softer.mydiary.entries.diary;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import meow.softer.mydiary.R;
import meow.softer.mydiary.entries.DiaryActivity;
import meow.softer.mydiary.shared.FileManager;
import meow.softer.mydiary.shared.ThemeManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

public class DiaryPhotoBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    public interface PhotoCallBack {
        void addPhoto(String fileName);

        void selectPhoto(Uri uri);
    }

    private RelativeLayout RL_diary_photo_dialog;
    private ImageView IV_diary_photo_add_a_photo, IV_diary_photo_select_a_photo;

    /**
     * Camera & select photo
     */
    private static final int REQUEST_START_CAMERA_CODE = 1;
    private static final int REQUEST_SELECT_IMAGE_CODE = 2;

    /**
     * File
     */
    private FileManager fileManager;
    private String tempFileName;

    private PhotoCallBack callBack;


    public static DiaryPhotoBottomSheet newInstance(boolean isEditMode) {
        Bundle args = new Bundle();
        DiaryPhotoBottomSheet fragment = new DiaryPhotoBottomSheet();
        args.putBoolean("isEditMode", isEditMode);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (getArguments().getBoolean("isEditMode", false)) {
            fileManager = new FileManager(getActivity(), FileManager.DIARY_EDIT_CACHE_DIR);
        } else {
            fileManager = new FileManager(getActivity(), ((DiaryActivity) getActivity()).getTopicId());
        }
        try {
            callBack = (PhotoCallBack) getTargetFragment();
//            Activity activity = getActivity();
//            if (activity instanceof PhotoCallBack) {
//                callBack = (PhotoCallBack) activity;
//            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.bottom_sheet_diary_photo, container);
        RL_diary_photo_dialog = rootView.findViewById(R.id.RL_diary_photo_dialog);
        RL_diary_photo_dialog.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        IV_diary_photo_add_a_photo = rootView.findViewById(R.id.IV_diary_photo_add_a_photo);
        IV_diary_photo_add_a_photo.setOnClickListener(this);
        IV_diary_photo_select_a_photo = rootView.findViewById(R.id.IV_diary_photo_select_a_photo);
        IV_diary_photo_select_a_photo.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_START_CAMERA_CODE) {
            if (resultCode == RESULT_OK) {
                callBack.addPhoto(tempFileName);
            }
            dismiss();
        } else if (requestCode == REQUEST_SELECT_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                //fix the ZenPhone C & HTC 626 crash issues
                if (data != null && data.getData() != null && callBack != null) {
                    callBack.selectPhoto(data.getData());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
                }
            }
            dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_diary_photo_add_a_photo:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                tempFileName = "/" + FileManager.createRandomFileName();
                File tmpFile = new File(fileManager.getDir(), tempFileName);
                Uri outputFileUri;

                //Fix the Android N+ file can't be send
                outputFileUri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getApplicationContext().getPackageName() + ".provider", tmpFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, REQUEST_START_CAMERA_CODE);
                break;
            case R.id.IV_diary_photo_select_a_photo:
                checkAndRequestReadPermission();
                checkAndRequestWritePermission();
                selectImage();
                break;
        }
    }

    //fix the photo selecting bug
    private int REQUEST_READ_CODE = 765;
    private int REQUEST_WRITE_CODE = 723;

    private void checkAndRequestReadPermission() {

        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Mytest", "diaryphotobottomsheet read permission not granted");
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_CODE);

        } else if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("Mytest", "diaryphotobottomsheet read permission has granted");
        }
    }

    private void checkAndRequestWritePermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Mytest", "diaryphotobottomsheet write permission not granted");
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_CODE);

        } else if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("Mytest", "diaryphotobottomsheet write permission has granted");
        }
    }

    private void selectImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mSelectImageLauncher.launch(intent);
            Log.e("Mytest", "diaryphotobottomsheet getimage started ");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Mytest", "diaryphotobottomsheet getimage failed");
        }

    }

    private ActivityResultLauncher<Intent> mSelectImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Log.e("Mytest", "diaryphotobottomsheet registerforresult got result");
        if (result.getResultCode() == Activity.RESULT_OK) {
            Log.e("Mytest", "diaryphotobottomsheet registerforresult code ok");
            //fix the ZenPhone C & HTC 626 crash issues
            Intent data=result.getData();
            if (data != null && data.getData() != null && callBack != null) {
                Log.e("Mytest", "diaryphotobottomsheet data.getData: "+data.getData().toString());
                callBack.selectPhoto(data.getData());
                Log.e("Mytest", "diaryphotobottomsheet callback selectPhoto invoked");
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
            }
            dismiss();
        }
    });

}
