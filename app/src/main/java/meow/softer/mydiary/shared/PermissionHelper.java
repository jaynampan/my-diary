package meow.softer.mydiary.shared;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import meow.softer.mydiary.R;

public class PermissionHelper {
    public static final int REQUEST_ACCESS_FINE_LOCATION_PERMISSION = 1;
    public static final int REQUEST_CAMERA_AND_WRITE_ES_PERMISSION = 2; //ES:external storage
    public static final int REQUEST_WRITE_ES_PERMISSION = 3;

    public static boolean checkPermission(Fragment fragment, int requestCode) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION_PERMISSION:
                if (ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                    return false;
                }
                //TODO:shouldshowrequestPermissionRationable
                break;
            case REQUEST_CAMERA_AND_WRITE_ES_PERMISSION:
                if (ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    fragment.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    return false;
                }
                //TODO:shouldshowrequestPermissionRationable
                break;
            case REQUEST_WRITE_ES_PERMISSION:
                if (ActivityCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    return false;
                }
                //TODO:shouldshowrequestPermissionRationable
                break;
        }
        return true;
    }
    public static boolean checkPermission(Activity activity, int requestCode) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION_PERMISSION:
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
                    return false;
                }
                //TODO:shouldshowrequestPermissionRationable
                break;
            case REQUEST_CAMERA_AND_WRITE_ES_PERMISSION:
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    return false;
                }
                //TODO:shouldshowrequestPermissionRationable
                break;
            case REQUEST_WRITE_ES_PERMISSION:
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    return false;
                }
                //TODO:shouldshowrequestPermissionRationable
                break;
        }
        return true;
    }

    public static boolean checkAllPermissionResult(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void showAddPhotoDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.diary_location_permission_title))
                .setMessage(context.getString(R.string.diary_photo_permission_content))
                .setPositiveButton(context.getString(R.string.dialog_button_ok), null);
        builder.show();
    }

    public static void showAccessDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.diary_location_permission_title))
                .setMessage(context.getString(R.string.diary_location_permission_content))
                .setPositiveButton(context.getString(R.string.dialog_button_ok), null);
        builder.show();
    }
    //TODO:maybe need onRequestPermissionsResult
}

