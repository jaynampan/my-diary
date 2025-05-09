package meow.softer.mydiary.entries.diary

import android.app.Activity
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.gson.Gson
import meow.softer.mydiary.R
import meow.softer.mydiary.backup.obj.BUDiaryEntries
import meow.softer.mydiary.backup.obj.BUDiaryItem
import meow.softer.mydiary.entries.BaseDiaryFragment
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.entries.diary.ClearDialogFragment.ClearDialogCallback
import meow.softer.mydiary.entries.diary.CopyPhotoTask.CopyPhotoCallBack
import meow.softer.mydiary.entries.diary.DiaryPhotoBottomSheet.PhotoCallBack
import meow.softer.mydiary.entries.diary.SaveDiaryTask.SaveDiaryCallBack
import meow.softer.mydiary.entries.diary.item.DiaryItemHelper
import meow.softer.mydiary.entries.diary.item.DiaryPhoto
import meow.softer.mydiary.entries.diary.item.DiaryText
import meow.softer.mydiary.entries.diary.item.DiaryTextTag
import meow.softer.mydiary.entries.diary.item.IDiaryRow
import meow.softer.mydiary.entries.diary.picker.DatePickerFragment
import meow.softer.mydiary.entries.diary.picker.TimePickerFragment
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.PermissionHelper
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.TimeTools
import meow.softer.mydiary.shared.ViewTools
import java.io.FileNotFoundException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Observable
import java.util.Observer

class DiaryFragment : BaseDiaryFragment(), View.OnClickListener, PhotoCallBack, Observer,
    SaveDiaryCallBack, CopyPhotoCallBack, OnDateSetListener, OnTimeSetListener,
    ClearDialogCallback {
    private val TAG = "DiaryFragment"

    private var LL_diary_item_content: LinearLayout? = null
    private var TV_diary_month: TextView? = null
    private var TV_diary_date: TextView? = null
    private var TV_diary_day: TextView? = null
    private var TV_diary_time: TextView? = null
    private var TV_diary_location: TextView? = null

    private var SP_diary_weather: Spinner? = null
    private var SP_diary_mood: Spinner? = null
    private var EDT_diary_title: EditText? = null
    private var IV_diary_location: ImageView? = null
    private var TV_diary_item_content_hint: TextView? = null


    /**
     * Permission
     */
    private var firstAllowLocationPermission = false
    private var firstAllowCameraPermission = false

    /**
     * Time
     */
    private var calendar: Calendar? = null
    private var timeTools: TimeTools? = null
    private val sdf = SimpleDateFormat("HH:mm")

    /**
     * diary item
     */
    private var diaryItemHelper: DiaryItemHelper? = null

    /**
     * File
     */
    private var diaryTempFileManager: FileManager? = null

    /**
     * Location
     */
    private var diaryHandler: DiaryHandler? = null
    private var diaryLocations: Location? = null
    private var locationManager: LocationManager? = null
    private var noLocation: String? = null
    private var isLocation = false
    private var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
        timeTools = TimeTools.getInstance(requireActivity().applicationContext)
        noLocation = getString(R.string.diary_no_location)
        diaryTempFileManager = FileManager(requireContext(), topicId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_diary, container, false)

        /**
         * UI
         */
        val scrollView_diary_content =
            rootView.findViewById<ScrollView?>(R.id.ScrollView_diary_content)
        ViewTools.setScrollBarColor(requireContext(), scrollView_diary_content)

        val RL_diary_info = rootView.findViewById<RelativeLayout>(R.id.RL_diary_info)
        RL_diary_info.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )

        val LL_diary_edit_bar = rootView.findViewById<LinearLayout>(R.id.LL_diary_edit_bar)
        LL_diary_edit_bar.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )

        val LL_diary_time_information =
            rootView.findViewById<LinearLayout>(R.id.LL_diary_time_information)
        LL_diary_time_information.setOnClickListener(this)
        TV_diary_month = rootView.findViewById<TextView>(R.id.TV_diary_month)
        TV_diary_date = rootView.findViewById<TextView>(R.id.TV_diary_date)
        TV_diary_day = rootView.findViewById<TextView>(R.id.TV_diary_day)
        TV_diary_time = rootView.findViewById<TextView>(R.id.TV_diary_time)
        TV_diary_location = rootView.findViewById<TextView>(R.id.TV_diary_location)
        rootView.findViewById<View?>(R.id.IV_diary_location_name_icon).visibility = View.VISIBLE

        SP_diary_weather = rootView.findViewById<Spinner>(R.id.SP_diary_weather)
        SP_diary_weather!!.visibility = View.VISIBLE
        SP_diary_mood = rootView.findViewById<Spinner>(R.id.SP_diary_mood)
        SP_diary_mood!!.visibility = View.VISIBLE

        EDT_diary_title = rootView.findViewById<EditText>(R.id.EDT_diary_title)
        EDT_diary_title!!.background.mutate().setColorFilter(
            ThemeManager.instance!!.getThemeMainColor(requireContext()),
            PorterDuff.Mode.SRC_ATOP
        )

        TV_diary_item_content_hint =
            rootView.findViewById<TextView>(R.id.TV_diary_item_content_hint)
        //For create diary
        LL_diary_item_content = rootView.findViewById<LinearLayout>(R.id.LL_diary_item_content)
        LL_diary_item_content!!.setOnClickListener(this)

        val IV_diary_menu = rootView.findViewById<ImageView?>(R.id.IV_diary_menu)
        IV_diary_location = rootView.findViewById<ImageView>(R.id.IV_diary_location)
        IV_diary_location!!.setOnClickListener(this)
        val IV_diary_photo = rootView.findViewById<ImageView>(R.id.IV_diary_photo)
        IV_diary_photo.setOnClickListener(this)
        val IV_diary_delete = rootView.findViewById<ImageView>(R.id.IV_diary_delete)
        IV_diary_delete.setVisibility(View.GONE)
        val IV_diary_clear = rootView.findViewById<ImageView>(R.id.IV_diary_clear)
        IV_diary_clear.setOnClickListener(this)
        val IV_diary_save = rootView.findViewById<ImageView>(R.id.IV_diary_save)
        IV_diary_save.setOnClickListener(this)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diaryHandler = DiaryHandler(this)
        initWeatherSpinner()
        initMoodSpinner()
        setCurrentTime(true)
        initLocationManager()
        initProgressDialog()
        diaryItemHelper = DiaryItemHelper(LL_diary_item_content!!)
        clearDiaryPage()
        //Revert the auto saved diary
        revertAutoSaveDiary()
    }

    override fun onStart() {
        super.onStart()
        diaryItemHelper!!.addObserver(this)
    }

    override fun onResume() {
        super.onResume()
        //For PermissionsResult
        if (firstAllowLocationPermission) {
            //startGetLocation();
            firstAllowLocationPermission = false
        }
        //For PermissionsResult
        if (firstAllowCameraPermission) {
            openPhotoBottomSheet()
            firstAllowCameraPermission = false
        }
    }


    override fun onPause() {
        super.onPause()
        //Auto Save the diary
        autoSaveDiary()
    }

    override fun onStop() {
        super.onStop()
        //Release the resource
        diaryItemHelper!!.deleteObserver(this)

        if (locationManager != null) {
            try {
                locationManager!!.removeUpdates(locationListener)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        diaryHandler!!.removeCallbacksAndMessages(null)
        progressDialog!!.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * Google Place API
         */
        val PLACE_PICKER_REQUEST = 1
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(activity, data)
                if (place.name != null || place.name != "") {
                    //try to spilt the string if it is a local
                    TV_diary_location!!.text = place.name
                    isLocation = true
                } else {
                    isLocation = false
                }
                initLocationIcon()
            }
            progressDialog!!.dismiss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionHelper.REQUEST_ACCESS_FINE_LOCATION_PERMISSION -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                firstAllowLocationPermission = true
            } else {
                PermissionHelper.showAccessDialog(requireContext())
            }

            PermissionHelper.REQUEST_CAMERA_AND_WRITE_ES_PERMISSION -> if (grantResults.isNotEmpty()
                && PermissionHelper.checkAllPermissionResult(grantResults)
            ) {
                firstAllowCameraPermission = true
            } else {
                PermissionHelper.showAddPhotoDialog(requireContext())
            }
        }
    }

    private fun loadFileFromTemp(fileName: String?) {
        try {
            val tempFileSrc =
                FileManager.FILE_HEADER + diaryTempFileManager!!.dirAbsolutePath + "/" + fileName
            val resizeBmp =
                BitmapFactory.decodeFile(diaryTempFileManager!!.dirAbsolutePath + "/" + fileName)
            if (resizeBmp != null) {
                val diaryPhoto = DiaryPhoto(requireActivity(), null, IDiaryRow.TYPE_PHOTO, null)
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
            } else {
                throw FileNotFoundException(tempFileSrc + "not found or bitmap is null")
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

    private fun setCurrentTime(updateCurrentTime: Boolean) {
        if (updateCurrentTime) {
            calendar!!.setTimeInMillis(System.currentTimeMillis())
        }
        TV_diary_month!!.text = timeTools!!.monthsFullName?.get(calendar!!.get(Calendar.MONTH))
        TV_diary_date!!.text = calendar!!.get(Calendar.DAY_OF_MONTH).toString()
        TV_diary_day!!.text =
            timeTools!!.daysFullName?.get(calendar!!.get(Calendar.DAY_OF_WEEK) - 1)
        TV_diary_time!!.text = sdf.format(calendar!!.getTime())
    }

    private fun initLocationManager() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    private fun initLocationIcon() {
        if (isLocation) {
            IV_diary_location!!.setImageResource(R.drawable.ic_location_on_white_24dp)
        } else {
            IV_diary_location!!.setImageResource(R.drawable.ic_location_off_white_24dp)
            TV_diary_location!!.text = noLocation
        }
    }

    private fun initProgressDialog() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage(getString(R.string.process_dialog_loading))
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(android.R.style.Widget_ProgressBar)
    }

    private fun initWeatherSpinner() {
        val weatherArrayAdapter =
            ImageArrayAdapter(requireActivity().baseContext, DiaryInfoHelper.weatherArray)
        SP_diary_weather!!.setAdapter(weatherArrayAdapter)
    }

    private fun initMoodSpinner() {
        val moodArrayAdapter =
            ImageArrayAdapter(requireActivity().baseContext, DiaryInfoHelper.moodArray)
        SP_diary_mood!!.setAdapter(moodArrayAdapter)
    }

    /**
     * Clear and set the UUI
     */
    private fun clearDiaryPage() {
        isLocation = false
        initLocationIcon()
        SP_diary_mood!!.setSelection(0)
        SP_diary_weather!!.setSelection(0)
        EDT_diary_title!!.setText("")
        diaryItemHelper!!.initDiary()
    }

    /**
     * The temp file only be clear when click clear button & diary save
     */
    private fun clearDiaryTemp() {
        diaryTempFileManager!!.clearDir()
        SPFManager.clearDiaryAutoSave(requireContext(), topicId)
    }

    private fun autoSaveDiary() {
        if (diaryItemHelper!!.itemSize > 0) {
            val diaryItemItemList: MutableList<BUDiaryItem?> = ArrayList<BUDiaryItem?>()
            for (x in 0..<diaryItemHelper!!.itemSize) {
                diaryItemItemList.add(
                    BUDiaryItem(
                        diaryItemHelper!!.get(x)!!.type,
                        diaryItemHelper!!.get(x)!!.position,
                        diaryItemHelper!!.get(x)!!.content
                    )
                )
            }
            var locationName = TV_diary_location!!.getText().toString()
            if (noLocation == locationName) {
                locationName = ""
            }
            val autoSaveDiary = BUDiaryEntries(
                BUDiaryEntries.NO_BU_DIARY_ID, BUDiaryEntries.NO_BU_DIARY_TIME,
                EDT_diary_title!!.getText().toString(),
                SP_diary_mood!!.selectedItemPosition,
                SP_diary_weather!!.selectedItemPosition,
                diaryItemHelper!!.nowPhotoCount > 0,
                locationName, diaryItemItemList
            )
            SPFManager.setDiaryAutoSave(requireContext(), topicId, Gson().toJson(autoSaveDiary))
        }
    }

    /**
     * Revert diray from SPF
     */
    private fun revertAutoSaveDiary() {
        if (SPFManager.getDiaryAutoSave(requireContext(), topicId) != null) {
            try {
                val autoSaveDiary = Gson().fromJson<BUDiaryEntries>(
                    SPFManager.getDiaryAutoSave(requireContext(), topicId),
                    BUDiaryEntries::class.java
                )
                //Title
                EDT_diary_title!!.setText(autoSaveDiary.diaryEntriesTitle)

                //load location
                val locationName = autoSaveDiary.diaryEntriesLocation
                if (locationName != null && "" != locationName) {
                    isLocation = true
                    TV_diary_location!!.text = locationName
                } else {
                    isLocation = false
                }
                initLocationIcon()
                setIcon(autoSaveDiary.diaryEntriesMood, autoSaveDiary.diaryEntriesWeather)
                loadDiaryItemContent(autoSaveDiary)
            } catch (e: Exception) {
                Log.e(TAG, "Load auto save fail", e)
            }
            TV_diary_item_content_hint!!.visibility = View.INVISIBLE
        } else {
            TV_diary_item_content_hint!!.visibility = View.VISIBLE
        }
    }

    private fun setIcon(mood: Int, weather: Int) {
        SP_diary_mood!!.setSelection(mood)
        SP_diary_weather!!.setSelection(weather)
    }

    private fun loadDiaryItemContent(autoSaveDiary: BUDiaryEntries) {
        for (i in autoSaveDiary.diaryItemList!!.indices) {
            var diaryItem: IDiaryRow? = null
            var content: String? = ""
            if (autoSaveDiary.diaryItemList[i]
                    !!.diaryItemType == IDiaryRow.TYPE_PHOTO
            ) {
                diaryItem = DiaryPhoto(requireActivity(), null, IDiaryRow.TYPE_PHOTO, null)
                content = FileManager.FILE_HEADER +
                        diaryTempFileManager!!.dirAbsolutePath + "/" +
                        autoSaveDiary.diaryItemList[i]!!.diaryItemContent
                diaryItem.setDeleteClickListener(this)
                //For get the right file name
                diaryItem.setPhotoFileName(
                    autoSaveDiary.diaryItemList[i]!!.diaryItemContent
                )
            } else if (autoSaveDiary.diaryItemList[i]
                    !!.diaryItemType == IDiaryRow.TYPE_TEXT
            ) {
                diaryItem = DiaryText(requireActivity(), null, IDiaryRow.TYPE_TEXT, null)
                content = autoSaveDiary.diaryItemList[i]!!.diaryItemContent
            }
            //In this page , it always is  edit mode.
            diaryItem!!.setEditMode(true)
            diaryItem.content = content
            diaryItem.position = i
            diaryItemHelper!!.createItem(diaryItem)
        }
    }

    private fun saveDiary() {
        //Create locationName
        var locationName = TV_diary_location!!.getText().toString()
        if (noLocation == locationName) {
            locationName = ""
        }
        SaveDiaryTask(
            requireActivity().baseContext,
            calendar!!.getTimeInMillis(),
            EDT_diary_title!!.getText().toString(),
            SP_diary_mood!!.selectedItemPosition,
            SP_diary_weather!!.selectedItemPosition,  //Check  attachment
            if (diaryItemHelper!!.nowPhotoCount > 0) true else false,
            locationName,
            diaryItemHelper!!,
            topicId,
            this
        ).execute(topicId)
    }

    //    private void startGetLocation() {
    //        //Open Google App or use geoCoder
    //        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS) {
    //            openGooglePlacePicker();
    //        } else {
    //            openGPSListener();
    //        }
    //    }
    //    private void openGooglePlacePicker() {
    //        try {
    //            progressDialog.show();
    //            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    //            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
    //        } catch (GooglePlayServicesRepairableException e) {
    //            e.printStackTrace();
    //            Toast.makeText(getActivity(), getString(R.string.toast_google_service_not_work), Toast.LENGTH_LONG).show();
    //            progressDialog.dismiss();
    //        } catch (GooglePlayServicesNotAvailableException e) {
    //            e.printStackTrace();
    //            Toast.makeText(getActivity(), getString(R.string.toast_google_service_not_work), Toast.LENGTH_LONG).show();
    //            progressDialog.dismiss();
    //        }
    //    }
    private fun openGPSListener() {
        progressDialog!!.show()
        try {
            if (locationManager!!.allProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
            }
            if (locationManager!!.allProviders.contains(LocationManager.GPS_PROVIDER)) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )
            }
            //Waiting gps max timeout is 20s
            diaryHandler!!.sendEmptyMessageDelayed(0, GPS_TIMEOUT.toLong())
        } catch (e: SecurityException) {
            //do nothing
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            diaryLocations = Location(location)
            diaryHandler!!.removeCallbacksAndMessages(null)
            diaryHandler!!.sendEmptyMessage(0)
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

    private fun openPhotoBottomSheet() {
        val diaryPhotoBottomSheet = DiaryPhotoBottomSheet.newInstance(false)
        diaryPhotoBottomSheet.setTargetFragment(this, 0)
        diaryPhotoBottomSheet.show(requireFragmentManager(), "diaryPhotoBottomSheet")
    }

    private fun checkoutOldDiaryContent(): DiaryTextTag? {
        val focusView = requireActivity().currentFocus
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

    override fun selectPhoto(uri: Uri?) {
        Log.e("Mytest", "diaryfragment selectphoto invoked")
        if (FileManager.isImage(
                FileManager.getFileNameByUri(requireContext(), uri!!).toString()
            )
        ) {
            //1.Copy bitmap to temp for rotating & resize
            //2.Then Load bitmap call back ;
            CopyPhotoTask(
                requireActivity(),
                uri,
                DiaryItemHelper.getVisibleWidth(requireActivity()),
                DiaryItemHelper.getVisibleHeight(
                    requireActivity()
                ),
                diaryTempFileManager!!,
                this
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
            diaryTempFileManager!!, this
        ).execute()
    }

    override fun onCopyCompiled(fileName: String?) {
        loadFileFromTemp(fileName)
    }


    override fun update(observable: Observable?, data: Any?) {
        if (diaryItemHelper!!.itemSize > 0) {
            TV_diary_item_content_hint!!.visibility = View.GONE
        } else {
            TV_diary_item_content_hint!!.visibility = View.VISIBLE
        }
    }

    override fun onDiarySaved() {
        //For next diary
        setCurrentTime(true)
        //Clear
        clearDiaryPage()
        clearDiaryTemp()
        //Set flag
        (activity as DiaryActivity).callEntriesListRefresh()
        //Goto entries page
        (activity as DiaryActivity).gotoPage(0)
    }

    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        //Since JellyBean, the onDateSet() method of the DatePicker class is called twice
        if (view.isShown()) {
            calendar!!.set(year, monthOfYear, dayOfMonth)
            setCurrentTime(false)
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
            setCurrentTime(false)
        }
    }

    override fun onClear() {
        clearDiaryPage()
        clearDiaryTemp()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.LL_diary_time_information -> {
                val datePickerFragment =
                    DatePickerFragment.newInstance(calendar!!.getTimeInMillis())
                datePickerFragment.setOnDateSetListener(this)
                datePickerFragment.show(requireFragmentManager(), "datePickerFragment")
            }

            R.id.LL_diary_item_content -> if (diaryItemHelper!!.itemSize == 0) {
                diaryItemHelper!!.initDiary()
                //Add default edittext item
                val diaryText = DiaryText(requireActivity(), null, IDiaryRow.TYPE_TEXT, null)
                diaryText.position = diaryItemHelper!!.itemSize
                diaryItemHelper!!.createItem(diaryText)
                //set Focus
                diaryText.view!!.requestFocus()
                //Show keyboard automatically
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(diaryText.view, InputMethodManager.SHOW_IMPLICIT)
            } else if (diaryItemHelper!!.itemSize == 1) {
                //Make the soft keyboard can be opened when it is only one item.
                diaryItemHelper!!.get(0)!!.view!!.requestFocus()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(
                    diaryItemHelper!!.get(diaryItemHelper!!.itemSize - 1)!!.view,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }

            R.id.IV_diary_photo_delete -> {
                val position = v.tag as Int
                diaryItemHelper!!.remove(position)
                LL_diary_item_content!!.removeViewAt(position)
                diaryItemHelper!!.mergerAdjacentText(position)
                diaryItemHelper!!.resortPosition()
            }

            R.id.IV_diary_location -> if (isLocation) {
                isLocation = false
                initLocationIcon()
            } else {
                if (PermissionHelper.checkPermission(
                        requireActivity(),
                        PermissionHelper.REQUEST_ACCESS_FINE_LOCATION_PERMISSION
                    )
                ) {
                    //Check gps is open
                    if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    ) {
                        //startGetLocation();
                    } else {
                        Toast.makeText(
                            activity,
                            getString(R.string.toast_location_not_open),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            R.id.IV_diary_photo -> if (FileManager.sDCardFreeSize > FileManager.MIN_FREE_SPACE) {
                if (PermissionHelper.checkPermission(
                        requireActivity(),
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

            R.id.IV_diary_clear -> if (diaryItemHelper!!.itemSize > 0 || EDT_diary_title!!.length() > 0 || SP_diary_mood!!.selectedItemPosition > 0 || SP_diary_weather!!.selectedItemPosition > 0) {
                val clearDialogFragment = ClearDialogFragment()
                clearDialogFragment.setTargetFragment(this, 0)
                clearDialogFragment.show(requireFragmentManager(), "clearDialogFragment")
            }

            R.id.IV_diary_save -> if (diaryItemHelper!!.itemSize > 0) {
                saveDiary()
            } else {
                Toast.makeText(
                    activity,
                    getString(R.string.toast_diary_empty),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private class DiaryHandler(aFragment: DiaryFragment?) : Handler() {
        private val mFrag: WeakReference<DiaryFragment?> = WeakReference<DiaryFragment?>(aFragment)

        override fun handleMessage(msg: Message) {
            val theFrag = mFrag.get()
            if (theFrag != null) {
                theFrag.TV_diary_location!!.text = getLocationName(theFrag)
                theFrag.initLocationIcon()
            }
        }

        fun getLocationName(theFrag: DiaryFragment): String {
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
                                theFrag.isLocation = true
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
                    theFrag.isLocation = false
                }
            }
            return returnLocation.toString()
        }
    }

    companion object {
        private val GPS_TIMEOUT = 20 * 1000
    }
}
