package meow.softer.mydiary.main

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.yalantis.ucrop.UCrop
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.ViewTools
import meow.softer.mydiary.shared.gui.MyDiaryButton
import java.io.File
import meow.softer.mydiary.R


class DiaryDialogFragment : DialogFragment(), View.OnClickListener {
    private var readPermissionLauncher: ActivityResultLauncher<String?>? = null

    interface YourNameCallback {
        fun updateName()
    }


    /**
     * Callback
     */
    private var callback: YourNameCallback? = null

    /**
     * File
     */
    private var tempFileManager: FileManager? = null

    /**
     * Profile picture
     */
    private var profilePictureFileName: String? = ""
    private var isAddNewProfilePicture = false

    /**
     * UI
     */
    private var LL_your_name_content: LinearLayout? = null
    private var IV_your_name_profile_picture: ImageView? = null
    private var IV_your_name_profile_picture_cancel: ImageView? = null
    private var EDT_your_name_name: EditText? = null
    private var But_your_name_ok: MyDiaryButton? = null
    private var But_your_name_cancel: MyDiaryButton? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as YourNameCallback
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readPermissionLauncher = registerForActivityResult<String?, Boolean?>(
            RequestPermission(),
            object : ActivityResultCallback<Boolean?> {
                override fun onActivityResult(isGranted: Boolean?) {
                    if (isGranted == true) {
                        Log.d("MyTest", "Granted Read")
                        getImage()
                    } else {
                        Log.d("MyTest", "Not Granted Read")
                    }
                }
            })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(true)
        val rootView: View = inflater.inflate(R.layout.dialog_fragment_your_name, container)
        LL_your_name_content = rootView.findViewById<LinearLayout?>(R.id.LL_your_name_content)
        LL_your_name_content!!.setBackgroundColor(
            ThemeManager.getInstance().getThemeMainColor(activity)
        )

        IV_your_name_profile_picture =
            rootView.findViewById<ImageView?>(R.id.IV_your_name_profile_picture)
        IV_your_name_profile_picture!!.setOnClickListener(this)
        IV_your_name_profile_picture_cancel =
            rootView.findViewById<ImageView?>(R.id.IV_your_name_profile_picture_cancel)
        IV_your_name_profile_picture_cancel!!.setOnClickListener(this)

        EDT_your_name_name = rootView.findViewById<EditText?>(R.id.EDT_your_name_name)
        EDT_your_name_name!!.setText(SPFManager.getYourName(activity))

        But_your_name_ok = rootView.findViewById<MyDiaryButton?>(R.id.But_your_name_ok)
        But_your_name_ok!!.setOnClickListener(this)
        But_your_name_cancel = rootView.findViewById<MyDiaryButton?>(R.id.But_your_name_cancel)
        But_your_name_cancel!!.setOnClickListener(this)

        loadProfilePicture()
        return rootView
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("mTest", "onActivityResult called")
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val resultUri = UCrop.getOutput(data)
                    IV_your_name_profile_picture!!.setImageBitmap(BitmapFactory.decodeFile(resultUri!!.path))
                    profilePictureFileName = FileManager.getFileNameByUri(activity, resultUri)
                    isAddNewProfilePicture = true
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_crop_profile_picture_fail),
                        Toast.LENGTH_LONG
                    ).show()
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    private fun loadProfilePicture() {
        IV_your_name_profile_picture!!.setImageDrawable(
            ThemeManager.getInstance().getProfilePictureDrawable(activity)
        )
    }


    private fun saveYourName() {
        //Save name
        SPFManager.setYourName(activity, EDT_your_name_name!!.getText().toString())
        //Save profile picture
        if (isAddNewProfilePicture) {
            //Remove the old file
            val bgFM = FileManager(activity, FileManager.SETTING_DIR)
            val oldProfilePictureFile = File(
                (bgFM.dirAbsolutePath
                        + "/" + ThemeManager.CUSTOM_PROFILE_PICTURE_FILENAME)
            )
            if (oldProfilePictureFile.exists()) {
                oldProfilePictureFile.delete()
            }
            if ("" != profilePictureFileName) {
                try {
                    //Copy the profile into setting dir
                    FileManager.copy(
                        File(tempFileManager!!.dirAbsolutePath + "/" + profilePictureFileName),
                        oldProfilePictureFile
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_save_profile_picture_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.IV_your_name_profile_picture -> {
                Log.e("Mytest", "yournamedialogfragment IV_your_name_profile_picture clicked")
                requestPhotoPermission()
            }

            R.id.IV_your_name_profile_picture_cancel -> {
                Log.e(
                    "Mytest",
                    "yournamedialogfragment IV_your_name_profile_picture_cancel clicked"
                )
                isAddNewProfilePicture = true
                profilePictureFileName = ""
                IV_your_name_profile_picture!!.setImageDrawable(
                    ViewTools.getDrawable(activity, R.drawable.ic_person_picture_default)
                )
                Toast.makeText(this.context, "Pic set to default", Toast.LENGTH_SHORT).show()
                Log.e("Mytest", "yournamedialogfragment set profile image to default")
            }

            R.id.But_your_name_ok -> {
                saveYourName()
                callback!!.updateName()
                dismiss()
            }

            R.id.But_your_name_cancel -> dismiss()
        }
    }


    private fun getImage() {
            try {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                mGetContentLauncher.launch(intent)
                Log.e("Mytest", "yourname getimage started ")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Mytest", "yourname getimage failed")
            }
        }

    private val mGetContentLauncher = registerForActivityResult<Intent?, ActivityResult?>(
        StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult? ->
            Log.e("Mytest", "yourname registerforresult got result")
            if (result!!.resultCode == Activity.RESULT_OK) {
                Log.e("Mytest", "yourname registerforresult code ok")
                val data = result.data
                val selectedImgUri = data!!.data
                Log.e("Mytest", "yourname got uri:$selectedImgUri")

                if (selectedImgUri != null) {
                    Log.e("Mytest", "yourname selectedImgUri data not null")
                    //Create fileManager for get temp folder
                    tempFileManager = FileManager(activity, FileManager.TEMP_DIR)
                    tempFileManager!!.clearDir()
                    //Compute the bg size
                    val photoSize = ScreenHelper.dpToPixel(resources, 50)
                    Log.e("Mytest", "yournamedialogfragment  photosize$photoSize")
                    val options = UCrop.Options()
                    options.setToolbarColor(
                        ThemeManager.getInstance().getThemeMainColor(activity)
                    )
                    options.setStatusBarColor(
                        ThemeManager.getInstance().getThemeDarkColor(activity)
                    )
                    UCrop.of(
                        data.data!!, Uri.fromFile(
                            File(
                                tempFileManager!!.dir
                                    .toString() + "/" + FileManager.createRandomFileName()
                            )
                        )
                    )
                        .withMaxResultSize(photoSize, photoSize)
                        .withAspectRatio(1f, 1f)
                        .withOptions(options)
                        .start(requireActivity(), this)
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_photo_intent_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })

    private fun requestPhotoPermission() {
        // For API < 33
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                getImage()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                readPermissionLauncher!!.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                readPermissionLauncher!!.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            // For API >= 33
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(), Manifest.permission.READ_MEDIA_IMAGES
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                this.getImage()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(), Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                readPermissionLauncher!!.launch(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                readPermissionLauncher!!.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
    }
}
