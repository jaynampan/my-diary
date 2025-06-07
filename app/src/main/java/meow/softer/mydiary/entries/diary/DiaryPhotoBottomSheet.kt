package meow.softer.mydiary.entries.diary

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.ThemeManager
import java.io.File

class DiaryPhotoBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    interface PhotoCallBack {
        fun addPhoto(fileName: String?)

        fun selectPhoto(uri: Uri?)
    }

    private var RL_diary_photo_dialog: RelativeLayout? = null
    private var IV_diary_photo_add_a_photo: ImageView? = null
    private var IV_diary_photo_select_a_photo: ImageView? = null

    /**
     * File
     */
    private var fileManager: FileManager? = null
    private var tempFileName: String? = null

    private var callBack: PhotoCallBack? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        fileManager = if (requireArguments().getBoolean("isEditMode", false)) {
            FileManager(requireContext(), FileManager.DIARY_EDIT_CACHE_DIR)
        } else {
            FileManager(requireContext(), (activity as DiaryActivity).topicId)
        }
        try {
            callBack = targetFragment as PhotoCallBack?
            //            Activity activity = getActivity();
//            if (activity instanceof PhotoCallBack) {
//                callBack = (PhotoCallBack) activity;
//            }
        } catch (e: ClassCastException) {
            e.printStackTrace()
            Toast.makeText(
                activity,
                getString(R.string.toast_photo_intent_error),
                Toast.LENGTH_LONG
            ).show()
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(true)
        val rootView = inflater.inflate(R.layout.bottom_sheet_diary_photo, container)
        RL_diary_photo_dialog = rootView.findViewById<RelativeLayout>(R.id.RL_diary_photo_dialog)
        RL_diary_photo_dialog!!.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )

        IV_diary_photo_add_a_photo =
            rootView.findViewById<ImageView>(R.id.IV_diary_photo_add_a_photo)
        IV_diary_photo_add_a_photo!!.setOnClickListener(this)
        IV_diary_photo_select_a_photo =
            rootView.findViewById<ImageView>(R.id.IV_diary_photo_select_a_photo)
        IV_diary_photo_select_a_photo!!.setOnClickListener(this)

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_START_CAMERA_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                callBack!!.addPhoto(tempFileName)
            }
            dismiss()
        } else if (requestCode == REQUEST_SELECT_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //fix the ZenPhone C & HTC 626 crash issues
                if (data != null && data.data != null && callBack != null) {
                    callBack!!.selectPhoto(data.data)
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_photo_intent_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            dismiss()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.IV_diary_photo_add_a_photo -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                tempFileName = "/" + FileManager.createRandomFileName()
                val tmpFile = File(fileManager!!.dir, tempFileName)

                //Fix the Android N+ file can't be send
                val outputFileUri = FileProvider.getUriForFile(
                    requireActivity(),
                    requireActivity().applicationContext.packageName + ".provider", tmpFile
                )

                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
                startActivityForResult(intent, REQUEST_START_CAMERA_CODE)
            }

            R.id.IV_diary_photo_select_a_photo -> {
                checkAndRequestReadPermission()
                checkAndRequestWritePermission()
                selectImage()
            }
        }
    }

    //fix the photo selecting bug
    private val REQUEST_READ_CODE = 765
    private val REQUEST_WRITE_CODE = 723

    private fun checkAndRequestReadPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Mytest", "diaryphotobottomsheet read permission not granted")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_CODE
            )
        } else if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Mytest", "diaryphotobottomsheet read permission has granted")
        }
    }

    private fun checkAndRequestWritePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Mytest", "diaryphotobottomsheet write permission not granted")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_CODE
            )
        } else if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Mytest", "diaryphotobottomsheet write permission has granted")
        }
    }

    private fun selectImage() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            mSelectImageLauncher.launch(intent)
            Log.e("Mytest", "diaryphotobottomsheet getimage started ")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Mytest", "diaryphotobottomsheet getimage failed")
        }
    }

    private val mSelectImageLauncher = registerForActivityResult<Intent?, ActivityResult?>(
        StartActivityForResult(),
        ActivityResultCallback { result: ActivityResult? ->
            Log.e("Mytest", "diaryphotobottomsheet registerforresult got result")
            if (result!!.resultCode == Activity.RESULT_OK) {
                Log.e("Mytest", "diaryphotobottomsheet registerforresult code ok")
                //fix the ZenPhone C & HTC 626 crash issues
                val data = result.data
                if (data != null && data.data != null && callBack != null) {
                    Log.e(
                        "Mytest",
                        "diaryphotobottomsheet data.getData: " + data.data.toString()
                    )
                    callBack!!.selectPhoto(data.data)
                    Log.e("Mytest", "diaryphotobottomsheet callback selectPhoto invoked")
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_photo_intent_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                dismiss()
            }
        })

    companion object {
        /**
         * Camera & select photo
         */
        private const val REQUEST_START_CAMERA_CODE = 1
        private const val REQUEST_SELECT_IMAGE_CODE = 2

        @JvmStatic
        fun newInstance(isEditMode: Boolean): DiaryPhotoBottomSheet {
            val args = Bundle()
            val fragment = DiaryPhotoBottomSheet()
            args.putBoolean("isEditMode", isEditMode)
            fragment.setArguments(args)
            return fragment
        }
    }
}
