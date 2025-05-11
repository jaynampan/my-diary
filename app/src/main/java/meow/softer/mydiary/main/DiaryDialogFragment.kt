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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.yalantis.ucrop.UCrop
import meow.softer.mydiary.MainActivity
import meow.softer.mydiary.R
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.ViewTools
import meow.softer.mydiary.ui.home.MainViewModel
import java.io.File


class DiaryDialogFragment : DialogFragment() {
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

    private lateinit var viewModel: MainViewModel

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
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        var YourNameIs = SPFManager.getYourName(requireContext())
        if (YourNameIs.isEmpty()) {
            YourNameIs = ThemeManager.instance!!.getThemeUserName(requireContext())
        }
        //TV_main_profile_username!!.text = YourNameIs
        viewModel.updateUserName(YourNameIs)
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
        val composeView = rootView.findViewById<ComposeView>(R.id.composeView)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ProfileDialogWrapper(
                    mainViewModel = viewModel,
                    onClick = { it ->
                        when (it) {
                            "Dismiss" -> {
                                dismiss()
                            }

                            "Confirm" -> {
                                saveYourName()
                                callback!!.updateName()
                                dismiss()
                            }

                            "Photo" -> {
                                Log.e(
                                    "Mytest",
                                    "yournamedialogfragment IV_your_name_profile_picture clicked"
                                )
                                requestPhotoPermission()
                            }

                            "Reset" -> {
                                Log.e(
                                    "Mytest",
                                    "yournamedialogfragment IV_your_name_profile_picture_cancel clicked"
                                )
                                isAddNewProfilePicture = true
                                profilePictureFileName = ""
                                viewModel.updateUserPic(
                                    BitmapPainter(
                                        AppCompatResources.getDrawable(
                                            requireContext(), R.drawable.ic_person_picture_default
                                        )!!.toBitmap()
                                            .asImageBitmap()
                                    )
                                )
                                Toast.makeText(
                                    this.context,
                                    "Pic set to default",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(
                                    "Mytest",
                                    "yournamedialogfragment set profile image to default"
                                )
                            }
                        }
                    }
                )
            }
        }
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
                    viewModel.updateUserPic(
                        BitmapPainter(
                            BitmapFactory.decodeFile(resultUri!!.path).asImageBitmap()
                        )
                    )
                    profilePictureFileName =
                        FileManager.getFileNameByUri(requireContext(), resultUri)
                    isAddNewProfilePicture = true
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_crop_profile_picture_fail),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun loadProfilePicture() {
        viewModel.updateUserPic(ThemeManager.instance!!.getProfilePicPainter(requireContext()))

    }


    private fun saveYourName() {
        //Save name
        SPFManager.setYourName(requireContext(), viewModel.userName.value)
        //Save profile picture
        if (isAddNewProfilePicture) {
            //Remove the old file
            val bgFM = FileManager(requireContext(), FileManager.SETTING_DIR)
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
                    tempFileManager = FileManager(requireContext(), FileManager.TEMP_DIR)
                    tempFileManager!!.clearDir()
                    //Compute the bg size
                    val photoSize = ScreenHelper.dpToPixel(resources, 50)
                    Log.e("Mytest", "yournamedialogfragment  photosize$photoSize")
                    val options = UCrop.Options()
                    options.setToolbarColor(
                        ThemeManager.instance!!.getThemeMainColor(requireContext())
                    )
                    options.setStatusBarColor(
                        ThemeManager.instance!!.getThemeDarkColor(requireContext())
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
