package meow.softer.mydiary.main;

import static android.app.Activity.RESULT_OK;

import static meow.softer.mydiary.shared.PermissionHelper.REQUEST_WRITE_ES_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import meow.softer.mydiary.R;
import meow.softer.mydiary.shared.FileManager;
import meow.softer.mydiary.shared.PermissionHelper;
import meow.softer.mydiary.shared.SPFManager;
import meow.softer.mydiary.shared.ScreenHelper;
import meow.softer.mydiary.shared.ThemeManager;
import meow.softer.mydiary.shared.ViewTools;
import meow.softer.mydiary.shared.gui.MyDiaryButton;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Map;

public class YourNameDialogFragment extends DialogFragment implements View.OnClickListener{


    public interface YourNameCallback {
        void updateName();
    }


    /**
     * Callback
     */
    private YourNameCallback callback;
    /**
     * File
     */
    private FileManager tempFileManager;
    private final static int SELECT_PROFILE_PICTURE_BG = 333;
    private final static int PICK_IMAGE_REQUEST = 444;
    /**
     * Profile picture
     */
    private String profilePictureFileName = "";
    private boolean isAddNewProfilePicture = false;

    /**
     * UI
     */
    private LinearLayout LL_your_name_content;
    private ImageView IV_your_name_profile_picture, IV_your_name_profile_picture_cancel;
    private EditText EDT_your_name_name;
    private MyDiaryButton But_your_name_ok, But_your_name_cancel;



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (YourNameCallback) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.dialog_fragment_your_name, container);
        LL_your_name_content = (LinearLayout) rootView.findViewById(R.id.LL_your_name_content);
        LL_your_name_content.setBackgroundColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));

        IV_your_name_profile_picture = (ImageView) rootView.findViewById(R.id.IV_your_name_profile_picture);
        IV_your_name_profile_picture.setOnClickListener(this);
        IV_your_name_profile_picture_cancel = (ImageView) rootView.findViewById(R.id.IV_your_name_profile_picture_cancel);
        IV_your_name_profile_picture_cancel.setOnClickListener(this);

        EDT_your_name_name = (EditText) rootView.findViewById(R.id.EDT_your_name_name);
        EDT_your_name_name.setText(SPFManager.getYourName(getActivity()));

        But_your_name_ok = (MyDiaryButton) rootView.findViewById(R.id.But_your_name_ok);
        But_your_name_ok.setOnClickListener(this);
        But_your_name_cancel = (MyDiaryButton) rootView.findViewById(R.id.But_your_name_cancel);
        But_your_name_cancel.setOnClickListener(this);

        loadProfilePicture();
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_ES_PERMISSION) {
            if (grantResults.length > 0
                    && PermissionHelper.checkAllPermissionResult(grantResults)) {
                FileManager.startBrowseImageFile(this.getActivity(), SELECT_PROFILE_PICTURE_BG);
            } else {
                PermissionHelper.showAddPhotoDialog(getActivity());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PROFILE_PICTURE_BG) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getData() != null) {
                    Log.e("Mytest", "yournamedialogfragment onactivityresult data not null");
                    //Create fileManager for get temp folder
                    tempFileManager = new FileManager(getActivity(), FileManager.TEMP_DIR);
                    tempFileManager.clearDir();
                    //Compute the bg size
                    int photoSize = ScreenHelper.dpToPixel(getResources(), 50);
                    Log.e("Mytest", "yournamedialogfragment onactivityresult photosize" + photoSize);
                    UCrop.Options options = new UCrop.Options();
                    options.setToolbarColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
                    options.setStatusBarColor(ThemeManager.getInstance().getThemeDarkColor(getActivity()));
                    UCrop.of(data.getData(), Uri.fromFile(
                                    new File(tempFileManager.getDir() + "/" + FileManager.createRandomFileName())))
                            .withMaxResultSize(photoSize, photoSize)
                            .withAspectRatio(1, 1)
                            .withOptions(options)
                            .start(getActivity(), this);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri resultUri = UCrop.getOutput(data);
                    IV_your_name_profile_picture.setImageBitmap(BitmapFactory.decodeFile(resultUri.getPath()));
                    profilePictureFileName = FileManager.getFileNameByUri(getActivity(), resultUri);
                    isAddNewProfilePicture = true;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.toast_crop_profile_picture_fail), Toast.LENGTH_LONG).show();
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }


    }

    private void loadProfilePicture() {
        IV_your_name_profile_picture.setImageDrawable(ThemeManager.getInstance().getProfilePictureDrawable(getActivity()));
    }


    private void saveYourName() {
        //Save name
        SPFManager.setYourName(getActivity(), EDT_your_name_name.getText().toString());
        //Save profile picture
        if (isAddNewProfilePicture) {
            //Remove the old file
            FileManager bgFM = new FileManager(getActivity(), FileManager.SETTING_DIR);
            File oldProfilePictureFile = new File(bgFM.getDirAbsolutePath()
                    + "/" + ThemeManager.CUSTOM_PROFILE_PICTURE_FILENAME);
            if (oldProfilePictureFile.exists()) {
                oldProfilePictureFile.delete();
            }
            if (!"".equals(profilePictureFileName)) {
                try {
                    //Copy the profile into setting dir
                    FileManager.copy(
                            new File(tempFileManager.getDirAbsolutePath() + "/" + profilePictureFileName),
                            oldProfilePictureFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.toast_save_profile_picture_fail), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_your_name_profile_picture:
                Log.e("Mytest", "yournamedialogfragment IV_your_name_profile_picture clicked");
                requestReadAndWritePermissions();
                break;
            case R.id.IV_your_name_profile_picture_cancel:
                Log.e("Mytest", "yournamedialogfragment IV_your_name_profile_picture_cancel clicked");
                isAddNewProfilePicture = true;
                profilePictureFileName = "";
                IV_your_name_profile_picture.setImageDrawable(
                        ViewTools.getDrawable(getActivity(), R.drawable.ic_person_picture_default));
                Toast.makeText(this.getContext(), "Pic set to default", Toast.LENGTH_SHORT).show();
                Log.e("Mytest", "yournamedialogfragment set profile image to default");
                break;
            case R.id.But_your_name_ok:
                saveYourName();
                callback.updateName();
                dismiss();
                break;
            case R.id.But_your_name_cancel:
                dismiss();
                break;
        }
    }

    private int  REQUEST_READ_AND_WRITE_CODE=943;
    private void checkAndRequestReadAndWritePermission(){
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
        ||ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_READ_AND_WRITE_CODE);
        }
    }
    private void checkReadAndWritePermissions(){
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(this.getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this.getContext(), "Storage permissions are required", Toast.LENGTH_SHORT).show();
            Log.e("Mytest","storage permissions not granted");
        }else{
            Log.e("Mytest","storage permissions have granted");
        }
    }
    public void getImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mGetContentLauncher.launch(intent);
            Log.e("Mytest", "yourname getimage started ");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Mytest", "yourname getimage failed");
        }

    }

    private ActivityResultLauncher<Intent> mGetContentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Log.e("Mytest", "yourname registerforresult got result");
        if (result.getResultCode() == Activity.RESULT_OK) {
            Log.e("Mytest", "yourname registerforresult code ok");
            Intent data = result.getData();
            Uri selectedImgUri = data.getData();
            Log.e("Mytest", "yourname got uri:" + selectedImgUri.toString());

            if (data != null && selectedImgUri != null) {
                Log.e("Mytest", "yourname selectedImgUri data not null");
                //Create fileManager for get temp folder
                tempFileManager = new FileManager(getActivity(), FileManager.TEMP_DIR);
                tempFileManager.clearDir();
                //Compute the bg size
                int photoSize = ScreenHelper.dpToPixel(getResources(), 50);
                Log.e("Mytest", "yournamedialogfragment  photosize" + photoSize);
                UCrop.Options options = new UCrop.Options();
                options.setToolbarColor(ThemeManager.getInstance().getThemeMainColor(getActivity()));
                options.setStatusBarColor(ThemeManager.getInstance().getThemeDarkColor(getActivity()));
                UCrop.of(data.getData(), Uri.fromFile(
                                new File(tempFileManager.getDir() + "/" + FileManager.createRandomFileName())))
                        .withMaxResultSize(photoSize, photoSize)
                        .withAspectRatio(1, 1)
                        .withOptions(options)
                        .start(getActivity(), this);
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_photo_intent_error), Toast.LENGTH_LONG).show();
            }

        }
    });
    private ActivityResultLauncher<String[]> requestPermissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),permissions->{
        int grantedNum=0;
        for (Map.Entry<String,Boolean>entry: permissions.entrySet()){
            Boolean isGranted=entry.getValue();
            if (isGranted){
                grantedNum+=1;
            }
        }
        if (grantedNum==2){
            Log.e("Mytest","permissionLauncher permissions has granted");
            getImage();
        }else{
            Log.e("Mytest","permissionLauncher permissions not granted");
        }
    });
    private void requestReadAndWritePermissions(){
        String[]permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissionLauncher.launch(permissions);
    }

}
