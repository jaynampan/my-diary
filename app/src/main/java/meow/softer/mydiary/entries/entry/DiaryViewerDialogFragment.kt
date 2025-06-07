package meow.softer.mydiary.entries.entry

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.ProgressDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.DialogFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlacePicker
import meow.softer.mydiary.R
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.entries.EditDiaryBackDialogFragment
import meow.softer.mydiary.entries.EditDiaryBackDialogFragment.BackDialogCallback
import meow.softer.mydiary.entries.diary.ClearDialogFragment
import meow.softer.mydiary.entries.diary.CopyPhotoTask
import meow.softer.mydiary.entries.diary.CopyPhotoTask.CopyPhotoCallBack
import meow.softer.mydiary.entries.diary.DiaryFragment
import meow.softer.mydiary.entries.diary.DiaryInfoHelper.getWeatherResourceId
import meow.softer.mydiary.entries.diary.DiaryInfoHelper.moodArray
import meow.softer.mydiary.entries.diary.DiaryInfoHelper.weatherArray
import meow.softer.mydiary.entries.diary.DiaryPhotoBottomSheet.Companion.newInstance
import meow.softer.mydiary.entries.diary.DiaryPhotoBottomSheet.PhotoCallBack
import meow.softer.mydiary.entries.diary.ImageArrayAdapter
import meow.softer.mydiary.entries.diary.item.DiaryItemHelper
import meow.softer.mydiary.entries.diary.item.DiaryPhoto
import meow.softer.mydiary.entries.diary.item.DiaryText
import meow.softer.mydiary.entries.diary.item.DiaryTextTag
import meow.softer.mydiary.entries.diary.item.IDiaryRow
import meow.softer.mydiary.entries.diary.picker.DatePickerFragment
import meow.softer.mydiary.entries.diary.picker.TimePickerFragment
import meow.softer.mydiary.entries.entry.CopyDiaryToEditCacheTask.EditTaskCallBack
import meow.softer.mydiary.entries.entry.DiaryDeleteDialogFragment.Companion.newInstance
import meow.softer.mydiary.entries.entry.UpdateDiaryTask.UpdateDiaryCallBack
import meow.softer.mydiary.entries.photo.PhotoDetailViewerActivity
import meow.softer.mydiary.entries.photo.PhotoOverviewActivity
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.PermissionHelper
import meow.softer.mydiary.shared.ScreenHelper
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.TimeTools
import meow.softer.mydiary.shared.ViewTools
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper
import meow.softer.mydiary.shared.statusbar.PhoneModel
import meow.softer.mydiary.ui.components.DiaryBottom
import java.io.FileNotFoundException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DiaryViewerDialogFragment : DialogFragment(), View.OnClickListener,
    DiaryDeleteDialogFragment.DeleteCallback, EditTaskCallBack, PhotoCallBack, CopyPhotoCallBack,
    UpdateDiaryCallBack, BackDialogCallback, OnDateSetListener, OnTimeSetListener {
    /**
     * Callback
     */
    interface DiaryViewerCallback {
        fun deleteDiary()

        fun updateDiary()
    }

    private var callback: DiaryViewerCallback? = null

    private var PB_diary_item_content_hint: ProgressBar? = null
    private var LL_diary_time_information: LinearLayout? = null

    private var TV_diary_month: TextView? = null
    private var TV_diary_date: TextView? = null
    private var TV_diary_day: TextView? = null
    private var TV_diary_time: TextView? = null

    private var IV_diary_weather: ImageView? = null
    private var IV_diary_location_name_icon: ImageView? = null
    private var TV_diary_weather: TextView? = null
    private var TV_diary_location: TextView? = null
    private var SP_diary_weather: Spinner? = null
    private var SP_diary_mood: Spinner? = null

    private var TV_diary_title_content: TextView? = null
    private var EDT_diary_title: EditText? = null

    private var LL_diary_item_content: LinearLayout? = null
    private var IV_diary_location: ImageView? = null
    private var IV_diary_photo: ImageView? = null
    private var IV_diary_delete: ImageView? = null
    private var IV_diary_clear: ImageView? = null
    private var IV_diary_save: ImageView? = null
    private var isEditMode = false

    /**
     * diary content & info
     */
    private var diaryId: Long = 0
    private var diaryItemHelper: DiaryItemHelper? = null
    private var diaryFileManager: FileManager? = null

    /**
     * Edit Mode
     */
    private var mTask: CopyDiaryToEditCacheTask? = null
    private var loadDiaryHandler: Handler? = null
    private var initHandlerOrTaskIsRunning = false

    private var calendar: Calendar? = null
    private var timeTools: TimeTools? = null
    private var sdf: SimpleDateFormat? = null

    /**
     * Diary Photo viewer
     */
    private var diaryPhotoFileList: ArrayList<Uri?>? = null

    /**
     * Google Place API
     */
    private val PLACE_PICKER_REQUEST = 1

    /**
     * Location
     */
    private var diaryViewerHandler: DiaryViewerHandler? = null
    private var diaryLocations: Location? = null
    private var locationManager: LocationManager? = null
    private var haveLocation = false
    private var noLocation: String? = null
    private var progressDialog: ProgressDialog? = null


    /**
     * Permission
     */
    private var firstAllowLocationPermission = false
    private var firstAllowCameraPermission = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEditMode = requireArguments().getBoolean("isEditMode", false)
        if (isEditMode) {
            Toast.makeText(
                activity,
                getString(R.string.toast_diary_long_click_edit), Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                if (isEditMode) {
                    val backDialogFragment = EditDiaryBackDialogFragment()
                    backDialogFragment.setTargetFragment(this@DiaryViewerDialogFragment, 0)
                    backDialogFragment.show(requireFragmentManager(), "backDialogFragment")
                } else {
                    super.onBackPressed()
                }
            }
        }
        // request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        //Set background is transparent , for dialog radius
        dialog.window!!.decorView.background.alpha = 0
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(false)
        val rootView = inflater.inflate(R.layout.fragment_diary, container)

        val composeBottom = rootView.findViewById<ComposeView>(R.id.compose_bottom)
        composeBottom.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DiaryBottom(
                    images = listOf(
                        R.drawable.ic_more_horiz_white_24dp,
                        R.drawable.ic_location_off_white_24dp,
                        R.drawable.ic_photo_camera_white_24dp,
                        R.drawable.ic_delete_white_24dp,
                        R.drawable.ic_clear_white_24dp,
                        R.drawable.ic_save_white_24dp
                    )
                ) {
                    when(it){
                        1 -> if (haveLocation) {
                            haveLocation = false
                            initLocationIcon()
                        } else {
                            if (PermissionHelper.checkPermission(
                                    this@DiaryViewerDialogFragment.requireActivity(),
                                    PermissionHelper.REQUEST_ACCESS_FINE_LOCATION_PERMISSION
                                )
                            ) {
                                //Check gps is open
                                if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                                    locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                                ) {
                                    startGetLocation()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        getString(R.string.toast_location_not_open),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        2 -> if (isEditMode) {
                            //Allow add photo
                            if (FileManager.sDCardFreeSize > FileManager.MIN_FREE_SPACE) {
                                if (PermissionHelper.checkPermission(
                                        this@DiaryViewerDialogFragment.requireActivity(),
                                        PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION
                                    )
                                ) {
                                    if (diaryItemHelper!!.nowPhotoCount < DiaryItemHelper.MAX_PHOTO_COUNT) {
                                        openPhotoBottomSheet()
                                    } else {
                                        Toast.makeText(
                                            activity,
                                            String.format(
                                                resources.getString(R.string.toast_max_photo),
                                                DiaryItemHelper.MAX_PHOTO_COUNT
                                            ),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                //Insufficient
                                Toast.makeText(
                                    activity,
                                    getString(R.string.toast_space_insufficient),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            //Show the gallery
                            val gotoPhotoOverviewIntent =
                                Intent(activity, PhotoOverviewActivity::class.java)
                            gotoPhotoOverviewIntent.putExtra(
                                PhotoOverviewActivity.PHOTO_OVERVIEW_TOPIC_ID,
                                (activity as DiaryActivity).topicId
                            )
                            gotoPhotoOverviewIntent.putExtra(
                                PhotoOverviewActivity.PHOTO_OVERVIEW_DIARY_ID,
                                diaryId
                            )
                            requireActivity().startActivity(gotoPhotoOverviewIntent)
                        }

                        R.id.IV_diary_close_dialog -> if (isEditMode) {
                            val backDialogFragment = EditDiaryBackDialogFragment()
                            backDialogFragment.setTargetFragment(this@DiaryViewerDialogFragment, 0)
                            backDialogFragment.show(requireFragmentManager(), "backDialogFragment")
                        } else {
                            dismiss()
                        }

                        3 -> {
                            val diaryDeleteDialogFragment =
                                newInstance((activity as DiaryActivity).topicId, diaryId)
                            diaryDeleteDialogFragment.setTargetFragment(this@DiaryViewerDialogFragment, 0)
                            diaryDeleteDialogFragment.show(requireFragmentManager(), "diaryDeleteDialogFragment")
                        }

                        4 -> dismiss()
                        5 -> if (diaryItemHelper!!.itemSize > 0) {
                            updateDiary()
                        } else {
                            Toast.makeText(
                                activity,
                                getString(R.string.toast_diary_empty),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        /**
         * UI
         */
        val scrollView_diary_content =
            rootView.findViewById<ScrollView?>(R.id.ScrollView_diary_content)
        ViewTools.setScrollBarColor(requireContext(), scrollView_diary_content)

        val RL_diary_info = rootView.findViewById<RelativeLayout>(R.id.RL_diary_info)
        RL_diary_info.background = ThemeManager.instance!!.createDiaryViewerInfoBg(requireContext())

//        val LL_diary_edit_bar = rootView.findViewById<LinearLayout>(R.id.LL_diary_edit_bar)
//        LL_diary_edit_bar.background = ThemeManager.instance!!.createDiaryViewerEditBarBg(requireContext())

        PB_diary_item_content_hint =
            rootView.findViewById<ProgressBar>(R.id.PB_diary_item_content_hint)

        EDT_diary_title = rootView.findViewById<EditText>(R.id.EDT_diary_title)

        TV_diary_month = rootView.findViewById<TextView>(R.id.TV_diary_month)
        TV_diary_date = rootView.findViewById<TextView>(R.id.TV_diary_date)
        TV_diary_day = rootView.findViewById<TextView>(R.id.TV_diary_day)
        TV_diary_time = rootView.findViewById<TextView>(R.id.TV_diary_time)

        IV_diary_location_name_icon =
            rootView.findViewById<ImageView>(R.id.IV_diary_location_name_icon)
        TV_diary_location = rootView.findViewById<TextView>(R.id.TV_diary_location)

        LL_diary_item_content = rootView.findViewById<LinearLayout>(R.id.LL_diary_item_content)

        val IV_diary_close_dialog = rootView.findViewById<ImageView>(R.id.IV_diary_close_dialog)
        IV_diary_close_dialog.setVisibility(View.VISIBLE)
        IV_diary_close_dialog.setOnClickListener(this)

//        IV_diary_location = rootView.findViewById<ImageView>(R.id.IV_diary_location)
//
//        IV_diary_photo = rootView.findViewById<ImageView>(R.id.IV_diary_photo)
//        IV_diary_delete = rootView.findViewById<ImageView>(R.id.IV_diary_delete)
//        IV_diary_clear = rootView.findViewById<ImageView>(R.id.IV_diary_clear)
//        IV_diary_save = rootView.findViewById<ImageView>(R.id.IV_diary_save)

        initView(rootView)
        diaryItemHelper = DiaryItemHelper(LL_diary_item_content!!)
        noLocation = getString(R.string.diary_no_location)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback = targetFragment as DiaryViewerCallback?
        diaryId = requireArguments().getLong("diaryId", -1L)
        //Init the object
        if (diaryId != -1L) {
            if (isEditMode) {
                diaryViewerHandler = DiaryViewerHandler(this)
                diaryFileManager = FileManager(requireContext(), FileManager.DIARY_EDIT_CACHE_DIR)
                diaryFileManager!!.clearDir()
                PB_diary_item_content_hint!!.visibility = View.VISIBLE
                mTask = CopyDiaryToEditCacheTask(requireActivity(), diaryFileManager!!, this)
                //Make ths ProgressBar show 0.7s+.
                loadDiaryHandler = Handler()
                initHandlerOrTaskIsRunning = true
                loadDiaryHandler!!.postDelayed(object : Runnable {
                    override fun run() {
                        //Copy the file into editCash
                        mTask!!.execute((activity as DiaryActivity).topicId, diaryId)
                    }
                }, 700)
            } else {
                diaryFileManager = FileManager(
                    requireContext(),
                    (activity as DiaryActivity).topicId,
                    diaryId
                )
                diaryPhotoFileList = ArrayList<Uri?>()
                initData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION) {
            if (grantResults.isNotEmpty()
                && PermissionHelper.checkAllPermissionResult(grantResults)
            ) {
                firstAllowCameraPermission = true
            } else {
                val builder = AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.diary_location_permission_title))
                    .setMessage(getString(R.string.diary_photo_permission_content))
                    .setPositiveButton(getString(R.string.dialog_button_ok), null)
                builder.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(activity, data)
                if (place.name != null || place.name != "") {
                    //try to spilt the string if it is a local
                    TV_diary_location!!.text = place.name
                    haveLocation = true
                } else {
                    haveLocation = false
                }
                initLocationIcon()
            }
            progressDialog!!.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        //For PermissionsResult
        if (firstAllowLocationPermission) {
            openGooglePlacePicker()
            firstAllowLocationPermission = false
        }
        if (firstAllowCameraPermission) {
            openPhotoBottomSheet()
            firstAllowCameraPermission = false
        }
    }

    override fun onStart() {
        super.onStart()
        //Modify dialog size
        val dialog = getDialog()
        if (dialog != null) {
            val dialogHeight: Int
            if (ChinaPhoneHelper.deviceStatusBarType== PhoneModel.OTHER) {
                dialogHeight = (ScreenHelper.getScreenHeight(requireContext())
                        - ScreenHelper.getStatusBarHeight(requireContext())
                        - ScreenHelper.dpToPixel(requireActivity().resources, 2 * 10))
            } else {
                dialogHeight = (ScreenHelper.getScreenHeight(requireContext())
                        - ScreenHelper.dpToPixel(requireActivity().resources, 2 * 10))
            }
            val dialogWidth = (ScreenHelper.getScreenWidth(requireContext())
                    - ScreenHelper.dpToPixel(requireActivity().resources, 2 * 5))
            dialog.window!!.setLayout(dialogWidth, dialogHeight)
        }
    }

    override fun onStop() {
        super.onStop()
        if (initHandlerOrTaskIsRunning) {
            if (loadDiaryHandler != null) {
                loadDiaryHandler!!.removeCallbacksAndMessages(null)
            }
            if (mTask != null) {
                mTask!!.cancel(true)
            }
            dismissAllowingStateLoss()
        }
        if (locationManager != null) {
            try {
                locationManager!!.removeUpdates(locationListener)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        if (diaryViewerHandler != null) {
            diaryViewerHandler!!.removeCallbacksAndMessages(null)
        }
        if (progressDialog != null) {
            progressDialog!!.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    private fun initData() {
        val dbManager = DBManager(activity)
        dbManager.openDB()
        val diaryInfoCursor = dbManager.selectDiaryInfoByDiaryId(diaryId)
        //load Time
        calendar = Calendar.getInstance()
        calendar!!.setTimeInMillis(diaryInfoCursor.getLong(1))
        timeTools = TimeTools.getInstance(requireActivity().applicationContext)
        sdf = SimpleDateFormat("HH:mm")
        setDiaryTime()
        if (isEditMode) {
            //Allow to edit diary
            LL_diary_time_information!!.setOnClickListener(this)
            EDT_diary_title!!.setText(diaryInfoCursor.getString(2))
        } else {
            var diaryTitleStr = diaryInfoCursor.getString(2)
            if (diaryTitleStr == null || diaryTitleStr == "") {
                diaryTitleStr = getString(R.string.diary_no_title)
            }
            TV_diary_title_content!!.text = diaryTitleStr
        }
        //load location
        val locationName = diaryInfoCursor.getString(7)
        if (locationName != null && "" != locationName) {
            haveLocation = true
            IV_diary_location_name_icon!!.setVisibility(View.VISIBLE)
            TV_diary_location!!.text = locationName
        } else {
            haveLocation = false
            IV_diary_location_name_icon!!.setVisibility(View.VISIBLE)
        }
        initLocationIcon()
        setIcon(diaryInfoCursor.getInt(3), diaryInfoCursor.getInt(4))
        diaryInfoCursor.close()
        //Get diary detail
        loadDiaryItemContent(dbManager)
        dbManager.closeDB()
    }

    private fun initView(rootView: View) {
        if (isEditMode) {
            initProgressDialog()
            initLocationManager()

            LL_diary_time_information =
                rootView.findViewById<LinearLayout>(R.id.LL_diary_time_information)
            SP_diary_mood = rootView.findViewById<Spinner>(R.id.SP_diary_mood)
            SP_diary_mood!!.visibility = View.VISIBLE
            SP_diary_weather = rootView.findViewById<Spinner>(R.id.SP_diary_weather)
            SP_diary_weather!!.visibility = View.VISIBLE

            //For hidden hint
            EDT_diary_title!!.setText(" ")
            EDT_diary_title!!.background.mutate().setColorFilter(
                ThemeManager.instance!!.getThemeMainColor(requireContext()),
                PorterDuff.Mode.SRC_ATOP
            )

            initMoodSpinner()
            initWeatherSpinner()
//            IV_diary_location!!.setOnClickListener(this)

//            IV_diary_delete!!.setOnClickListener(this)
//            IV_diary_clear!!.setVisibility(View.GONE)

//            IV_diary_photo!!.setImageResource(R.drawable.ic_photo_camera_white_24dp)
//            IV_diary_photo!!.setOnClickListener(this)
        } else {
            EDT_diary_title!!.visibility = View.GONE
            val RL_diary_weather = rootView.findViewById<RelativeLayout>(R.id.RL_diary_weather)
            RL_diary_weather.visibility = View.GONE
            val RL_diary_mood = rootView.findViewById<RelativeLayout>(R.id.RL_diary_mood)
            RL_diary_mood.visibility = View.GONE

            IV_diary_weather = rootView.findViewById<ImageView>(R.id.IV_diary_weather)
            TV_diary_weather = rootView.findViewById<TextView>(R.id.TV_diary_weather)
            IV_diary_weather!!.setVisibility(View.VISIBLE)
            TV_diary_weather!!.visibility = View.VISIBLE

            IV_diary_location_name_icon!!.setVisibility(View.VISIBLE)

            TV_diary_title_content = rootView.findViewById<TextView>(R.id.TV_diary_title_content)
            TV_diary_title_content!!.visibility = View.VISIBLE
            TV_diary_title_content!!.setTextColor(
                ThemeManager.instance!!.getThemeMainColor(requireContext())
            )

//            IV_diary_delete!!.setOnClickListener(this)
//            IV_diary_clear!!.setVisibility(View.GONE)
//            IV_diary_save!!.setVisibility(View.GONE)

//            IV_diary_photo!!.setImageResource(R.drawable.ic_photo_white_24dp)
//            IV_diary_photo!!.setOnClickListener(this)
        }
    }

    private fun initMoodSpinner() {
        val moodArrayAdapter = ImageArrayAdapter(requireActivity(), moodArray)
        SP_diary_mood!!.setAdapter(moodArrayAdapter)
    }

    private fun initWeatherSpinner() {
        val weatherArrayAdapter = ImageArrayAdapter(requireActivity(), weatherArray)
        SP_diary_weather!!.setAdapter(weatherArrayAdapter)
    }

    private fun loadDiaryItemContent(dbManager: DBManager) {
        val diaryContentCursor = dbManager.selectDiaryContentByDiaryId(diaryId)
        //To count how many photo is in the diary on view mode.
        var photoCount = 0
        for (i in 0..<diaryContentCursor.count) {
            var diaryItem: IDiaryRow? = null
            var content: String? = ""
            if (diaryContentCursor.getInt(1) == IDiaryRow.TYPE_PHOTO) {
                diaryItem = DiaryPhoto(requireActivity(), null, IDiaryRow.TYPE_PHOTO, null)
                content = FileManager.FILE_HEADER +
                        diaryFileManager!!.dirAbsolutePath + "/" + diaryContentCursor.getString(
                    3
                )
                if (isEditMode) {
                    diaryItem.setEditMode(true)
                    diaryItem.setDeleteClickListener(this)
                    //For get the right file name
                    diaryItem.setPhotoFileName(diaryContentCursor.getString(3))
                } else {
                    diaryItem.setEditMode(false)
                    diaryItem.setDraweeViewClickListener(this)
                    diaryItem.setDraweeViewPositionTag(photoCount)
                    photoCount++
                    diaryPhotoFileList!!.add(Uri.parse(content))
                }
            } else if (diaryContentCursor.getInt(1) == IDiaryRow.TYPE_TEXT) {
                diaryItem = DiaryText(requireActivity(), null, IDiaryRow.TYPE_TEXT, null)
                content = diaryContentCursor.getString(3)
                if (!isEditMode) {
                    diaryItem.setEditMode(false)
                }
            }
            diaryItem!!.content = content
            diaryItem.position = i
            diaryItemHelper!!.createItem(diaryItem)
            diaryContentCursor.moveToNext()
        }
        diaryContentCursor.close()
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage(getString(R.string.process_dialog_loading))
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
    }

    private fun initLocationManager() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    private fun initLocationIcon() {
        if (haveLocation) {
//            IV_diary_location!!.setImageResource(R.drawable.ic_location_on_white_24dp)
        } else {
//            IV_diary_location!!.setImageResource(R.drawable.ic_location_off_white_24dp)
            TV_diary_location!!.text = noLocation
        }
    }

    private fun startGetLocation() {
        //Open Google App or use geoCoder
        if (GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        ) {
            openGooglePlacePicker()
        } else {
            openGPSListener()
        }
    }

    private fun openGooglePlacePicker() {
        if (GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
        ) {
            try {
                progressDialog!!.show()
                val builder = PlacePicker.IntentBuilder()
                startActivityForResult(builder.build(activity), PLACE_PICKER_REQUEST)
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
                progressDialog!!.dismiss()
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
                progressDialog!!.dismiss()
            }
        } else {
            Toast.makeText(
                activity,
                getString(R.string.toast_google_service_not_work),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openGPSListener() {
        progressDialog!!.show()
        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 4000, 0f,
                locationListener
            )
            //Waiting gps max timeout is 15s
            diaryViewerHandler!!.sendEmptyMessageDelayed(0, 15000)
        } catch (e: SecurityException) {
            //do nothing
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            diaryLocations = Location(location)
            diaryViewerHandler!!.removeCallbacksAndMessages(null)
            diaryViewerHandler!!.sendEmptyMessage(0)
            try {
                locationManager!!.removeUpdates(this)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }


    private fun setDiaryTime() {
        TV_diary_month!!.text = timeTools!!.monthsFullName?.get(calendar!!.get(Calendar.MONTH))
        TV_diary_date!!.text = calendar!!.get(Calendar.DAY_OF_MONTH).toString()
        TV_diary_day!!.text = timeTools!!.daysFullName?.get(calendar!!.get(Calendar.DAY_OF_WEEK) - 1)
        TV_diary_time!!.text = sdf!!.format(calendar!!.getTime())
    }

    private fun setIcon(mood: Int, weather: Int) {
        if (isEditMode) {
            SP_diary_mood!!.setSelection(mood)
            SP_diary_weather!!.setSelection(weather)
        } else {
            IV_diary_weather!!.setImageResource(getWeatherResourceId(weather))
            TV_diary_weather!!.text = resources.getStringArray(R.array.weather_list)[weather]
        }
    }

    private fun loadFileFromTemp(fileName: String?) {
        try {
            val tempFileSrc =
                FileManager.FILE_HEADER + diaryFileManager!!.dirAbsolutePath + "/" + fileName
            //start test
            Log.e("Mytest", "DiaryViewerDialogFragment tmpPicSrc:$tempFileSrc")
            //Bitmap resizeBmp = BitmapFactory.decodeFile(tempFileSrc);
            val resizeBmp =
                BitmapFactory.decodeFile(diaryFileManager!!.dirAbsolutePath + "/" + fileName)
            if (resizeBmp == null) {
                throw FileNotFoundException("$tempFileSrc not found or bitmap is null")
            } else {
                Log.e(
                    "Mytest",
                    "DiaryViewerDialogFragment resizeBmp size: " + resizeBmp.getByteCount()
                )
            }
            //end test
            val diaryPhoto = DiaryPhoto(activity, null, IDiaryRow.TYPE_PHOTO, null)
            diaryPhoto.setPhoto(Uri.parse(tempFileSrc), fileName)
            val tag = checkoutOldDiaryContent()
            //Check edittext is focused
            if (tag != null) {
                //Add new edittext
                val diaryText = DiaryText(requireActivity(), null, IDiaryRow.TYPE_TEXT, null)
                diaryText.position = tag.positionTag
                diaryText.content = tag.nextEditTextStr
                diaryItemHelper!!.createItem(diaryText, tag.positionTag + 1)
                diaryText.view!!.requestFocus()
                //Add photo
                diaryPhoto.position = tag.positionTag + 1
                diaryPhoto.setDeleteClickListener(this)
                diaryItemHelper!!.createItem(diaryPhoto, tag.positionTag + 1)
            } else {
                //Add photo
                diaryPhoto.position = diaryItemHelper!!.itemSize
                diaryPhoto.setDeleteClickListener(this)
                diaryItemHelper!!.createItem(diaryPhoto)
                //Add new edittext
                val diaryText = DiaryText(requireActivity(), null, IDiaryRow.TYPE_TEXT, null)
                diaryText.position = diaryItemHelper!!.itemSize
                diaryItemHelper!!.createItem(diaryText)
                diaryText.view!!.requestFocus()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            Toast.makeText(
                activity,
                getString(R.string.toast_photo_path_error),
                Toast.LENGTH_LONG
            ).show()
        } finally {
            diaryItemHelper!!.resortPosition()
        }
    }

    private fun checkoutOldDiaryContent(): DiaryTextTag? {
        val focusView = dialog!!.currentFocus
        var tag: DiaryTextTag? = null
        if (focusView is EditText && focusView.tag != null &&
            focusView.tag is DiaryTextTag
        ) {
            val currentEditText = focusView
            tag = focusView.tag as DiaryTextTag?
            if (currentEditText.getText().toString().isNotEmpty()) {
                val index = currentEditText.selectionStart
                val nextEditTextStr = currentEditText.getText().toString()
                    .substring(index, currentEditText.getText().toString().length)
                currentEditText.getText().delete(index, currentEditText.getText().toString().length)
                tag!!.nextEditTextStr = nextEditTextStr
            }
        }
        return tag
    }

    private fun updateDiary() {
        //Create locationName

        var locationName = TV_diary_location!!.getText().toString()
        if (noLocation == locationName) {
            locationName = ""
        }
        UpdateDiaryTask(
            requireContext(), calendar!!.getTimeInMillis(), EDT_diary_title!!.getText().toString(),
            SP_diary_mood!!.selectedItemPosition, SP_diary_weather!!.selectedItemPosition,
            locationName,  //Check  attachment
            if (diaryItemHelper!!.nowPhotoCount > 0) true else false,
            diaryItemHelper!!, diaryFileManager!!, this
        ).execute((activity as DiaryActivity).topicId, diaryId)
    }

    private fun openPhotoBottomSheet() {
        val diaryPhotoBottomSheet = newInstance(true)
        diaryPhotoBottomSheet.setTargetFragment(this, 0)
        diaryPhotoBottomSheet.show(requireFragmentManager(), "diaryPhotoBottomSheet")
    }

    override fun onDiaryUpdated() {
        this.dismissAllowingStateLoss()
        callback!!.updateDiary()
    }

    override fun selectPhoto(uri: Uri?) {
        Log.e("Mytest", "diaryviewerdialogfragment selectphoto invoked")
        if (FileManager.isImage(
                FileManager.getFileNameByUri(requireContext(), uri!!).toString()
            )
        ) {
            //1.Copy bitmap to temp for rotating & resize
            //2.Then Load bitmap call back ;
            CopyPhotoTask(
                requireActivity(), uri,
                DiaryItemHelper.getVisibleWidth(requireActivity()), DiaryItemHelper.getVisibleHeight(
                    requireActivity()
                ),
                diaryFileManager!!, this
            ).execute()
        } else {
            Toast.makeText(activity, getString(R.string.toast_not_image), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun addPhoto(fileName: String?) {
        //1.get saved file for rotating & resize from temp
        //2.Then , Load bitmap in call back ;
        CopyPhotoTask(
            requireActivity(), fileName,
            DiaryItemHelper.getVisibleWidth(requireActivity()), DiaryItemHelper.getVisibleHeight(
                requireActivity()
            ),
            diaryFileManager!!, this
        ).execute()
    }

    override fun onCopyCompiled(fileName: String?) {
        loadFileFromTemp(fileName)
    }


    override fun onDiaryDelete() {
        callback!!.deleteDiary()
        dismiss()
    }

    override fun onBack() {
        dismiss()
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        //Since JellyBean, the onDateSet() method of the DatePicker class is called twice
        if (view.isShown()) {
            calendar!!.set(year, monthOfYear, dayOfMonth)
            setDiaryTime()
            val timePickerFragment = TimePickerFragment.newInstance(calendar!!.getTimeInMillis())
            timePickerFragment.setOnTimeSetListener(this)
            timePickerFragment.show(requireFragmentManager(), "timePickerFragment")
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        //Since JellyBean, the onTimeSet() method of the TimePicker class is called twice
        if (view.isShown()) {
            calendar!!.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar!!.set(Calendar.MINUTE, minute)
            setDiaryTime()
        }
    }


    override fun onCopyToEditCacheCompiled(result: Int) {
        if (result == CopyDiaryToEditCacheTask.RESULT_COPY_SUCCESSFUL) {
            PB_diary_item_content_hint!!.visibility = View.GONE
            initData()
            //Open the click listener
            IV_diary_clear!!.setOnClickListener(this)
            IV_diary_save!!.setOnClickListener(this)
        } else {
            dismissAllowingStateLoss()
        }
        initHandlerOrTaskIsRunning = false
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.LL_diary_time_information -> {
                val datePickerFragment =
                    DatePickerFragment.newInstance(calendar!!.getTimeInMillis())
                datePickerFragment.setOnDateSetListener(this)
                datePickerFragment.show(requireFragmentManager(), "datePickerFragment")
            }



            R.id.IV_diary_photo_delete -> {
                val deletePosition = v.tag as Int
                Log.e("test", "deletePosition = $deletePosition")
                diaryItemHelper!!.remove(deletePosition)
                LL_diary_item_content!!.removeViewAt(deletePosition)
                diaryItemHelper!!.mergerAdjacentText(deletePosition)
                diaryItemHelper!!.resortPosition()
            }

            R.id.SDV_diary_new_photo -> {
                val draweeViewPosition = v.tag as Int
                val gotoPhotoDetailViewer =
                    Intent(activity, PhotoDetailViewerActivity::class.java)
                gotoPhotoDetailViewer.putParcelableArrayListExtra(
                    PhotoDetailViewerActivity.DIARY_PHOTO_FILE_LIST, diaryPhotoFileList
                )
                gotoPhotoDetailViewer.putExtra(
                    PhotoDetailViewerActivity.SELECT_POSITION,
                    draweeViewPosition
                )
                requireActivity().startActivity(gotoPhotoDetailViewer)
            }


        }
    }

    private class DiaryViewerHandler(aFragment: DiaryViewerDialogFragment?) : Handler() {
        private val mFrag: WeakReference<DiaryViewerDialogFragment?>

        init {
            mFrag = WeakReference<DiaryViewerDialogFragment?>(aFragment)
        }

        override fun handleMessage(msg: Message) {
            val theFrag = mFrag.get()
            if (theFrag != null) {
                theFrag.TV_diary_location!!.text = getLocationName(theFrag)
                theFrag.initLocationIcon()
            }
        }

        fun getLocationName(theFrag: DiaryViewerDialogFragment): String {
            val returnLocation = StringBuilder()
            try {
                if (theFrag.diaryLocations != null) {
                    val providerList = theFrag.locationManager!!.allProviders
                    if (null != theFrag.diaryLocations && null != providerList && providerList.isNotEmpty()) {
                        val longitude = theFrag.diaryLocations!!.longitude
                        val latitude = theFrag.diaryLocations!!.latitude
                        val geocoder = Geocoder(
                            theFrag.requireActivity().applicationContext,
                            Locale.getDefault()
                        )
                        val listAddresses = geocoder.getFromLocation(latitude, longitude, 1)
                        if (null != listAddresses && listAddresses.isNotEmpty()) {
                            try {
                                returnLocation.append(listAddresses[0]!!.countryName)
                                returnLocation.append(" ")
                                returnLocation.append(listAddresses[0]!!.adminArea)
                                returnLocation.append(" ")
                                returnLocation.append(listAddresses[0]!!.locality)
                                theFrag.haveLocation = true
                            } catch (e: Exception) {
                                //revert it in finally
                            }
                        } else {
                            Toast.makeText(
                                theFrag.activity,
                                theFrag.getString(R.string.toast_geocoder_fail),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        theFrag.activity,
                        theFrag.getString(R.string.toast_location_timeout),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    theFrag.activity,
                    theFrag.getString(R.string.toast_geocoder_fail),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                theFrag.diaryLocations = null
                try {
                    theFrag.locationManager!!.removeUpdates(theFrag.locationListener)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
                theFrag.progressDialog!!.dismiss()
                if (returnLocation.isEmpty()) {
                    returnLocation.append(theFrag.noLocation)
                    theFrag.haveLocation = false
                }
            }
            return returnLocation.toString()
        }
    }

    companion object {
        private const val TAG = "DiaryViewer"

        @JvmStatic
        fun newInstance(diaryId: Long, isEditMode: Boolean): DiaryViewerDialogFragment {
            val args = Bundle()
            val fragment = DiaryViewerDialogFragment()
            args.putLong("diaryId", diaryId)
            args.putBoolean("isEditMode", isEditMode)
            fragment.setArguments(args)
            return fragment
        }
    }
}
