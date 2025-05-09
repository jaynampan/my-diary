package meow.softer.mydiary.shared

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import meow.softer.mydiary.R

object PermissionHelper {
    const val REQUEST_ACCESS_FINE_LOCATION_PERMISSION: Int = 1
    const val REQUEST_CAMERA_AND_WRITE_ES_PERMISSION: Int = 2 //ES:external storage
    const val REQUEST_WRITE_ES_PERMISSION: Int = 3

    fun checkPermission(fragment: Fragment, requestCode: Int): Boolean {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION_PERMISSION -> if (ActivityCompat.checkSelfPermission(
                    fragment.activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                fragment.requestPermissions(
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    requestCode
                )
                return false
            }

            REQUEST_CAMERA_AND_WRITE_ES_PERMISSION -> if (ActivityCompat.checkSelfPermission(
                    fragment.activity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    fragment.activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                fragment.requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), requestCode
                )
                return false
            }

            REQUEST_WRITE_ES_PERMISSION -> if (ActivityCompat.checkSelfPermission(
                    fragment.activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                fragment.requestPermissions(
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode
                )
                return false
            }
        }
        return true
    }

    fun checkPermission(activity: Activity, requestCode: Int): Boolean {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION_PERMISSION -> if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activity.requestPermissions(
                    arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                    requestCode
                )
                return false
            }

            REQUEST_CAMERA_AND_WRITE_ES_PERMISSION -> if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activity.requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), requestCode
                )
                return false
            }

            REQUEST_WRITE_ES_PERMISSION -> if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                activity.requestPermissions(
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    requestCode
                )
                return false
            }
        }
        return true
    }

    fun checkAllPermissionResult(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun showAddPhotoDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.diary_location_permission_title))
            .setMessage(context.getString(R.string.diary_photo_permission_content))
            .setPositiveButton(context.getString(R.string.dialog_button_ok), null)
        builder.show()
    }

    fun showAccessDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.diary_location_permission_title))
            .setMessage(context.getString(R.string.diary_location_permission_content))
            .setPositiveButton(context.getString(R.string.dialog_button_ok), null)
        builder.show()
    } //TODO:maybe need onRequestPermissionsResult
}

