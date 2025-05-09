package meow.softer.mydiary.main

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.yalantis.ucrop.UCrop
import meow.softer.mydiary.R
import meow.softer.mydiary.main.ColorPickerFragment.Companion.newInstance
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.PermissionHelper
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.gui.MyDiaryButton
import java.io.File

class TopicDetailDialogFragment : DialogFragment(), View.OnClickListener,
    ColorPickerFragment.colorPickerCallback {
    interface TopicCreatedCallback {
        fun TopicCreated(topicTitle: String?, type: Int, color: Int)

        fun TopicUpdated(
            position: Int,
            newTopicTitle: String?,
            color: Int,
            topicBgStatus: Int,
            newBgFileName: String?
        )
    }


    /**
     * Callback
     */
    private var callback: TopicCreatedCallback? = null
    private var isEditMode = false
    private var position = 0
    private var title: String? = null
    private var topicId: Long = 0
    private var topicType = 0
    private var topicColorCode = 0
    private var topicBgStatus: Int = TOPIC_BG_NORMAL
    private var newTopicBgFileName: String? = ""

    /**
     * UI
     */
    private var LL_topic_detail_content: LinearLayout? = null
    private var EDT_topic_detail_title: EditText? = null
    private var LL_topic_detail_default_bg: LinearLayout? = null
    private var RL_topic_detail_topic_bg: RelativeLayout? = null
    private var IV_topic_color: ImageView? = null
    private var IV_topic_detail_topic_bg: ImageView? = null
    private var But_topic_detail_default_bg: MyDiaryButton? = null
    private var SP_topic_detail_type: Spinner? = null
    private var But_topic_detail_ok: MyDiaryButton? = null
    private var But_topic_detail_cancel: MyDiaryButton? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as TopicCreatedCallback
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        isEditMode = requireArguments().getBoolean("isEditMode", false)
        position = requireArguments().getInt("position", -1)
        title = requireArguments().getString("title", "")
        topicId = requireArguments().getLong("topicId", -1)
        topicType = requireArguments().getInt("topicType", -1)
        topicColorCode = requireArguments().getInt("topicColorCode", Color.BLACK)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(false)
        //This position is wrong.
        if (isEditMode && (position == -1 || topicId == -1L || topicType == -1)) {
            dismiss()
        }

        val rootView = inflater.inflate(R.layout.dialog_fragment_topic_detail, container)
        LL_topic_detail_content = rootView.findViewById<LinearLayout?>(R.id.LL_topic_detail_content)
        LL_topic_detail_content!!.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )

        EDT_topic_detail_title = rootView.findViewById<EditText?>(R.id.EDT_topic_detail_title)
        IV_topic_color = rootView.findViewById<ImageView?>(R.id.IV_topic_color)
        IV_topic_color!!.setOnClickListener(this)
        setTextColor(topicColorCode)

        But_topic_detail_ok = rootView.findViewById<MyDiaryButton?>(R.id.But_topic_detail_ok)
        But_topic_detail_ok!!.setOnClickListener(this)
        But_topic_detail_cancel =
            rootView.findViewById<MyDiaryButton?>(R.id.But_topic_detail_cancel)
        But_topic_detail_cancel!!.setOnClickListener(this)

        if (isEditMode) {
            RL_topic_detail_topic_bg =
                rootView.findViewById<RelativeLayout?>(R.id.RL_topic_detail_topic_bg)
            RL_topic_detail_topic_bg!!.visibility = View.VISIBLE

            LL_topic_detail_default_bg =
                rootView.findViewById<LinearLayout?>(R.id.LL_topic_detail_default_bg)
            LL_topic_detail_default_bg!!.visibility = View.VISIBLE

            IV_topic_detail_topic_bg =
                rootView.findViewById<ImageView?>(R.id.IV_topic_detail_topic_bg)
            IV_topic_detail_topic_bg!!.setImageDrawable(
                ThemeManager.instance!!.getTopicBgDrawable(requireContext(), topicId, topicType)
            )
            IV_topic_detail_topic_bg!!.setOnClickListener(this)

            But_topic_detail_default_bg =
                rootView.findViewById<MyDiaryButton?>(R.id.But_topic_detail_default_bg)
            But_topic_detail_default_bg!!.visibility = View.VISIBLE
            But_topic_detail_default_bg!!.setOnClickListener(this)
            //Check current topic bg is default or not.
            But_topic_detail_default_bg!!.setEnabled(this.isTopicHaveCustomBg)

            EDT_topic_detail_title!!.setText(title)
        } else {
            SP_topic_detail_type = rootView.findViewById<Spinner?>(R.id.SP_topic_detail_type)
            SP_topic_detail_type!!.visibility = View.VISIBLE
            initTopicTypeSpinner()
        }
        return rootView
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionHelper.REQUEST_WRITE_ES_PERMISSION) {
            if (grantResults.isNotEmpty()
                && PermissionHelper.checkAllPermissionResult(grantResults)
            ) {
                FileManager.startBrowseImageFile(this.requireActivity(), SELECT_TOPIC_BG)
            } else {
                PermissionHelper.showAddPhotoDialog(requireContext())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_TOPIC_BG) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.data != null) {
                    val topicBgWidth = ThemeManager.instance!!.getTopicBgWidth(requireContext())

                    val topicBgHeight: Int = if (topicType == ITopic.TYPE_DIARY) {
                        ThemeManager.instance!!.getTopicBgHeight(requireContext())
                    } else {
                        ThemeManager.instance!!.getTopicBgWithoutEditBarHeight(requireContext())
                    }
                    val tempFileManager = FileManager(requireContext(), FileManager.TEMP_DIR)
                    //Clear the old photo file
                    tempFileManager.clearDir()
                    val options = UCrop.Options()
                    options.setToolbarColor(
                        ThemeManager.instance!!.getThemeMainColor(requireContext())
                    )
                    options.setStatusBarColor(
                        ThemeManager.instance!!.getThemeDarkColor(requireContext())
                    )
                    UCrop.of(
                        data.data!!,
                        Uri.fromFile(
                            File(
                                tempFileManager.dir
                                    .toString() + "/" + FileManager.createRandomFileName()
                            )
                        )
                    )
                        .withMaxResultSize(topicBgWidth, topicBgHeight)
                        .withAspectRatio(topicBgWidth.toFloat(), topicBgHeight.toFloat())
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
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val resultUri = UCrop.getOutput(data)
                    IV_topic_detail_topic_bg!!.setImageBitmap(BitmapFactory.decodeFile(resultUri!!.path))
                    newTopicBgFileName = FileManager.getFileNameByUri(requireContext(), resultUri)
                    But_topic_detail_default_bg!!.setEnabled(true)
                    topicBgStatus = TOPIC_BG_ADD_PHOTO
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_crop_profile_banner_fail),
                        Toast.LENGTH_LONG
                    ).show()
                    //sample error
                    // final Throwable cropError = UCrop.getError(data);
                }
            }
        }
    }

    private val isTopicHaveCustomBg: Boolean
        get() {
            val topicBgFile = ThemeManager.instance!!.getTopicBgSavePathFile(
                requireContext(), topicId, topicType
            )
            return topicBgFile.exists()
        }

    private fun setTextColor(colorCode: Int) {
        IV_topic_color!!.setImageDrawable(colorCode.toDrawable())
    }

    private fun initTopicTypeSpinner() {
        val topicTypeAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireActivity(), R.layout.spinner_simple_text,
            resources.getStringArray(R.array.topic_type)
        )
        SP_topic_detail_type!!.setAdapter(topicTypeAdapter)
        SP_topic_detail_type!!.setSelection(1)
    }

    override fun onColorChange(colorCode: Int, viewId: Int) {
        topicColorCode = colorCode
        setTextColor(colorCode)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.IV_topic_color -> {
                val secColorPickerFragment =
                    newInstance(topicColorCode, R.id.IV_topic_color)
                secColorPickerFragment.setTargetFragment(this, 0)
                secColorPickerFragment.show(requireFragmentManager(), "topicTextColorPickerFragment")
            }

            R.id.IV_topic_detail_topic_bg -> if (PermissionHelper.checkPermission(
                    this.requireActivity(),
                    PermissionHelper.REQUEST_WRITE_ES_PERMISSION
                )
            ) {
                FileManager.startBrowseImageFile(this.requireActivity(), SELECT_TOPIC_BG)
            }

            R.id.But_topic_detail_default_bg -> {
                topicBgStatus = TOPIC_BG_REVERT_DEFAULT
                newTopicBgFileName = ""
                IV_topic_detail_topic_bg!!.setImageDrawable(
                    ThemeManager.instance!!.getTopicBgDefaultDrawable(requireContext(), topicType)
                )
                But_topic_detail_default_bg!!.setEnabled(false)
            }

            R.id.But_topic_detail_ok -> if (isEditMode) {
                if (EDT_topic_detail_title!!.getText().toString().isNotEmpty()) {
                    callback!!.TopicUpdated(
                        position, EDT_topic_detail_title!!.getText().toString(),
                        topicColorCode, topicBgStatus, newTopicBgFileName
                    )
                    dismiss()
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_topic_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (EDT_topic_detail_title!!.getText().toString().isNotEmpty()) {
                    callback!!.TopicCreated(
                        EDT_topic_detail_title!!.getText().toString(),
                        SP_topic_detail_type!!.selectedItemPosition, topicColorCode
                    )
                    dismiss()
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_topic_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            R.id.But_topic_detail_cancel -> dismiss()
        }
    }

    companion object {
        /**
         * Edit
         */
        //topicBgStatus
        const val TOPIC_BG_NORMAL: Int = 0
        const val TOPIC_BG_ADD_PHOTO: Int = 1
        const val TOPIC_BG_REVERT_DEFAULT: Int = 3

        /**
         * File
         */
        private const val SELECT_TOPIC_BG = 0
        fun newInstance(
            isEditMode: Boolean, position: Int, topicId: Long,
            title: String?, topicType: Int, topicColorCode: Int
        ): TopicDetailDialogFragment {
            val args = Bundle()
            val fragment = TopicDetailDialogFragment()
            args.putBoolean("isEditMode", isEditMode)
            args.putInt("position", position)
            args.putString("title", title)
            args.putLong("topicId", topicId)
            args.putInt("topicType", topicType)
            args.putInt("topicColorCode", topicColorCode)
            fragment.setArguments(args)
            return fragment
        }
    }
}