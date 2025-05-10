package meow.softer.mydiary

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.Target
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.main.MainSettingDialogFragment
import meow.softer.mydiary.main.MainTopicAdapter
import meow.softer.mydiary.main.ReleaseNoteDialogFragment
import meow.softer.mydiary.main.TopicDeleteDialogFragment
import meow.softer.mydiary.main.TopicDetailDialogFragment
import meow.softer.mydiary.main.TopicDetailDialogFragment.TopicCreatedCallback
import meow.softer.mydiary.main.DiaryDialogFragment
import meow.softer.mydiary.main.DiaryDialogFragment.YourNameCallback
import meow.softer.mydiary.main.topic.Contacts
import meow.softer.mydiary.main.topic.Diary
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.main.topic.Memo
import meow.softer.mydiary.oobe.CustomViewTarget
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.gui.MyDiaryButton
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper
import meow.softer.mydiary.shared.statusbar.OOBE
import meow.softer.mydiary.ui.components.HomeHeader
import meow.softer.mydiary.ui.home.MainViewModel
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity(), View.OnClickListener, TopicCreatedCallback,
    YourNameCallback,
    TopicDeleteDialogFragment.DeleteCallback, TextWatcher {
    private var isExit: Boolean

    init {
        // Back button event
        isExit = false

    }


    //RecyclerView
    private var RecyclerView_topic: RecyclerView? = null
    private var mainTopicAdapter: MainTopicAdapter? = null
    private var topicList: MutableList<ITopic>? = null

    //swipe
    private var mRecyclerViewSwipeManager: RecyclerViewSwipeManager? = null
    private var mWrappedAdapter: RecyclerView.Adapter<*>? = null
    private var mRecyclerViewTouchActionGuardManager: RecyclerViewTouchActionGuardManager? = null

    //drag
    private var mRecyclerViewDragDropManager: RecyclerViewDragDropManager? = null

    /*
     * DB
     */
    private var dbManager: DBManager? = null


    private val backTimer = Timer()

    /*
     * OOBE
     */
    private var oobeCount = 0
    private var sv: ShowcaseView? = null

    /*
     * UI
     */
    private var themeManager: ThemeManager? = null
    private var IV_main_profile_picture: ImageView? = null

    private var LL_main_profile: LinearLayout? = null
    private var TV_main_profile_username: TextView? = null
    private var EDT_main_topic_search: EditText? = null
    private var IV_main_setting: ImageView? = null

    private var rootView: View? = null
    private var globalLayoutListener: OnGlobalLayoutListener? = null
    private val keyboardHeightThreshold = 300
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var composeView: ComposeView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set status bar
        ChinaPhoneHelper.setStatusBar(this, true)
        themeManager = ThemeManager.instance
        //LL_main_profile = findViewById<LinearLayout?>(R.id.LL_main_profile)
        //LL_main_profile!!.setOnClickListener(this)

        composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val userName = mainViewModel.userName.collectAsStateWithLifecycle().value
                val userPic = mainViewModel.userPic.collectAsStateWithLifecycle().value
                val profilePic = mainViewModel.userPainter.collectAsStateWithLifecycle().value
                    ?: painterResource(R.drawable.ic_person_picture_default)
                val bgPainter = mainViewModel.headerBgPainter.collectAsStateWithLifecycle().value
                    ?: painterResource(R.drawable.profile_theme_bg_taki)
                HomeHeader(
                    profilePic = profilePic,
                    bgPainter = bgPainter,
                    userName = userName,
                    onClick = {
                        val diaryDialogFragment = DiaryDialogFragment()
                        diaryDialogFragment.show(supportFragmentManager, "yourNameDialogFragment")
                    }
                )
            }
        }

        EDT_main_topic_search = findViewById<EditText?>(R.id.EDT_main_topic_search)
        EDT_main_topic_search!!.addTextChangedListener(this)

        IV_main_setting = findViewById<ImageView?>(R.id.IV_main_setting)
        IV_main_setting!!.setOnClickListener(this)

        RecyclerView_topic = findViewById<RecyclerView?>(R.id.RecyclerView_topic)
        rootView = findViewById<View?>(android.R.id.content)

        topicList = ArrayList<ITopic>()
        dbManager = DBManager(this@MainActivity)

        initProfile()
        initBottomBar()
        initTopicAdapter()
        loadProfilePicture()


        //Init topic adapter
        dbManager!!.openDB()
        loadTopic()
        dbManager!!.closeDB()
        mainTopicAdapter!!.notifyDataSetChanged(true)


        //listen the edit text
        autoClearEditTextFocus()


        initOOBE()

        //Check show Release note dialog.
        if (SPFManager.getFirstRun(this)) {
            if (intent.getBooleanExtra("showReleaseNote", false)) {
                val releaseNoteDialogFragment = ReleaseNoteDialogFragment()
                releaseNoteDialogFragment.show(
                    supportFragmentManager,
                    "releaseNoteDialogFragment"
                )
            }
        }

        SPFManager.setFirstRun(this, false)

    }

    private fun updateTopicBg(position: Int, topicBgStatus: Int, newTopicBgFileName: String?) {
        when (topicBgStatus) {
            TopicDetailDialogFragment.TOPIC_BG_ADD_PHOTO -> {
                val outputFile = themeManager!!.getTopicBgSavePathFile(
                    this, mainTopicAdapter!!.getList()[position]!!.id,
                    mainTopicAdapter!!.getList()[position]!!.type
                )
                //Copy file into topic dir
                try {
                    if (outputFile.exists()) {
                        outputFile.delete()
                    }
                    val tempFM = FileManager(this, FileManager.TEMP_DIR)
                    FileUtils.moveFile(
                        File(
                            (tempFM.dirAbsolutePath
                                    + "/" + newTopicBgFileName)
                        ),
                        outputFile
                    )
                    //Enter the topic
                    mainTopicAdapter!!.gotoTopic(
                        mainTopicAdapter!!.getList()[position]!!.type, position
                    )
                } catch (e: IOException) {
                    Toast.makeText(this, getString(R.string.topic_topic_bg_fail), Toast.LENGTH_LONG)
                        .show()
                    e.printStackTrace()
                }
            }

            TopicDetailDialogFragment.TOPIC_BG_REVERT_DEFAULT -> {
                val topicBgFile = themeManager!!.getTopicBgSavePathFile(
                    this, mainTopicAdapter!!.getList()[position]!!.id,
                    mainTopicAdapter!!.getList()[position]!!.type
                )
                //Just delete the file  , the topic's activity will check file for changing the bg
                if (topicBgFile.exists()) {
                    topicBgFile.delete()
                }
            }
        }
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        mainTopicAdapter!!.filter.filter(s)
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun onClick(v: View) {
        when (v.id) {
//            R.id.LL_main_profile -> {
//                val diaryDialogFragment = DiaryDialogFragment()
//                diaryDialogFragment.show(supportFragmentManager, "yourNameDialogFragment")
//            }

            R.id.IV_main_setting -> {
                val mainSettingDialogFragment = MainSettingDialogFragment()
                mainSettingDialogFragment.show(
                    supportFragmentManager,
                    "mainSettingDialogFragment"
                )
            }
        }
    }

    override fun onTopicDelete(position: Int) {
        val dbManager = DBManager(this@MainActivity)
        dbManager.openDB()
        when (mainTopicAdapter!!.getList()[position]!!.type) {
            ITopic.TYPE_CONTACTS -> dbManager.delAllContactsInTopic(
                mainTopicAdapter!!.getList()[position]!!.id
            )

            ITopic.TYPE_MEMO -> {
                dbManager.delAllMemoInTopic(mainTopicAdapter!!.getList()[position]!!.id)
                dbManager.deleteAllCurrentMemoOrder(
                    mainTopicAdapter!!.getList()[position]!!.id
                )
            }

            ITopic.TYPE_DIARY -> {
                //Clear the auto save content
                SPFManager.clearDiaryAutoSave(
                    this,
                    mainTopicAdapter!!.getList()[position]!!.id
                )
                //Because FOREIGN key is not work in this version,
                //so delete diary item first , then delete diary
                val diaryCursor =
                    dbManager.selectDiaryList(mainTopicAdapter!!.getList()[position]!!.id)
                var i = 0
                while (i < diaryCursor.count) {
                    dbManager.delAllDiaryItemByDiaryId(diaryCursor.getLong(0))
                    diaryCursor.moveToNext()
                    i++
                }
                diaryCursor.close()
                dbManager.delAllDiaryInTopic(mainTopicAdapter!!.getList()[position]!!.id)
            }
        }
        //Delete the dir if it exist.
        try {
            FileUtils.deleteDirectory(
                FileManager(
                    this@MainActivity,
                    mainTopicAdapter!!.getList()[position]!!.type,
                    mainTopicAdapter!!.getList()[position]!!.id
                ).dir
            )
        } catch (e: IOException) {
            //Do nothing if delete fail
            e.printStackTrace()
        }
        dbManager.delTopic(mainTopicAdapter!!.getList()[position]!!.id)
        //Don't delete the topic order, it will be refreshed next moving time.
        dbManager.closeDB()
        //Search for remove the topiclist
        for (i in topicList!!.indices) {
            if (topicList!![i].id == mainTopicAdapter!!.getList()[position]!!.id) {
                topicList!!.removeAt(i)
                break
            }
        }
        //remove the filter list
        mainTopicAdapter!!.getList().removeAt(position)
        //Notify recycle view
        mainTopicAdapter!!.notifyItemRemoved(position)
        mainTopicAdapter!!.notifyItemRangeChanged(position, mainTopicAdapter!!.itemCount)
        //Clear the filter
        EDT_main_topic_search!!.setText("")
    }


    override fun onStart() {
        super.onStart()
        //It should be reload
        countTopicContent()
    }

    override fun onPause() {
        mRecyclerViewDragDropManager!!.cancelDrag()
        super.onPause()
    }

    override fun onDestroy() {
        if (mRecyclerViewDragDropManager != null) {
            mRecyclerViewDragDropManager!!.release()
            mRecyclerViewDragDropManager = null
        }
        if (mRecyclerViewSwipeManager != null) {
            mRecyclerViewSwipeManager!!.release()
            mRecyclerViewSwipeManager = null
        }

        if (mRecyclerViewTouchActionGuardManager != null) {
            mRecyclerViewTouchActionGuardManager!!.release()
            mRecyclerViewTouchActionGuardManager = null
        }

        if (RecyclerView_topic != null) {
            RecyclerView_topic!!.setItemAnimator(null)
            RecyclerView_topic!!.setAdapter(null)
            RecyclerView_topic = null
        }

        if (mWrappedAdapter != null) {
            WrapperAdapterUtils.releaseAll(mWrappedAdapter)
            mWrappedAdapter = null
        }
        mainTopicAdapter = null
        super.onDestroy()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!isExit) {
            isExit = true
            Toast.makeText(this, getString(R.string.main_activity_exit_app), Toast.LENGTH_SHORT)
                .show()
            val task = object : TimerTask() {
                override fun run() {
                    isExit = false
                }
            }
            backTimer.schedule(task, 2000)
        } else {
            super.onBackPressed()
        }
    }

    private fun initProfile() {
        var YourNameIs = SPFManager.getYourName(this@MainActivity)
        if (YourNameIs.isEmpty()) {
            YourNameIs = themeManager!!.getThemeUserName(this@MainActivity)
        }
        //TV_main_profile_username!!.text = YourNameIs
        mainViewModel.updateUserName(YourNameIs)
        //LL_main_profile!!.background = themeManager!!.getProfileBgDrawable(this)
        mainViewModel.updateHeaderBgPic(themeManager!!.getProfileBgPainter(this))
    }

    private fun initBottomBar() {
        EDT_main_topic_search!!.background.setColorFilter(
            themeManager!!.getThemeMainColor(this),
            PorterDuff.Mode.SRC_ATOP
        )
        IV_main_setting!!.setColorFilter(themeManager!!.getThemeMainColor(this))
    }

    private fun loadTopic() {
        topicList!!.clear()
        val topicCursor = dbManager!!.selectTopic()
        for (i in 0..<topicCursor.count) {
            when (topicCursor.getInt(2)) {
                ITopic.TYPE_CONTACTS -> topicList!!.add(
                    Contacts(
                        topicCursor.getLong(0),
                        topicCursor.getString(1), topicCursor.getInt(5)
                    )
                )

                ITopic.TYPE_DIARY -> topicList!!.add(
                    Diary(
                        topicCursor.getLong(0),
                        topicCursor.getString(1),
                        topicCursor.getInt(5)
                    )
                )

                ITopic.TYPE_MEMO -> topicList!!.add(
                    Memo(
                        topicCursor.getLong(0),
                        topicCursor.getString(1),
                        topicCursor.getInt(5)
                    )
                )
            }
            topicCursor.moveToNext()
        }
        topicCursor.close()
    }

    private fun loadProfilePicture() {
        //IV_main_profile_picture!!.setImageDrawable(themeManager!!.getProfilePictureDrawable(this))
        mainViewModel.updateUserPic(themeManager!!.getProfilePicPainter(this))

    }

    private fun countTopicContent() {
        dbManager!!.openDB()
        for (i in topicList!!.indices) {
            when (topicList!![i].type) {
                ITopic.TYPE_CONTACTS -> topicList!![i].count =
                    dbManager!!.getContactsCountByTopicId(topicList!![i].id).toLong()

                ITopic.TYPE_DIARY -> topicList!![i].count =
                    dbManager!!.getDiaryCountByTopicId(topicList!![i].id).toLong()

                ITopic.TYPE_MEMO -> topicList!![i].count =
                    dbManager!!.getMemoCountByTopicId(topicList!![i].id).toLong()
            }
        }
        dbManager!!.closeDB()
        mWrappedAdapter!!.notifyDataSetChanged()
    }

    private fun initTopicAdapter() {
        // For swipe

        // swipe manager

        mRecyclerViewSwipeManager = RecyclerViewSwipeManager()

        // touch guard manager  (this class is required to suppress scrolling while swipe-dismiss animation is running)
        mRecyclerViewTouchActionGuardManager = RecyclerViewTouchActionGuardManager()
        mRecyclerViewTouchActionGuardManager!!.setInterceptVerticalScrollingWhileAnimationRunning(
            true
        )
        mRecyclerViewTouchActionGuardManager!!.setEnabled(true)

        //Init topic adapter
        val lmr = LinearLayoutManager(this)
        RecyclerView_topic!!.setLayoutManager(lmr)
        RecyclerView_topic!!.setHasFixedSize(true)
        mainTopicAdapter = MainTopicAdapter(this, topicList!!, dbManager!!)
        mWrappedAdapter = mRecyclerViewSwipeManager!!.createWrappedAdapter(mainTopicAdapter!!)


        val animator: GeneralItemAnimator = DraggableItemAnimator()

        // Change animations are enabled by default since support-v7-recyclerview v22.
        // Disable the change animation in order to make turning back animation of swiped item works properly.
        animator.supportsChangeAnimations = false


        //For Drag

        // Setup D&D feature and RecyclerView
        mRecyclerViewDragDropManager = RecyclerViewDragDropManager()

        mRecyclerViewDragDropManager!!.setInitiateOnMove(false)
        mRecyclerViewDragDropManager!!.setInitiateOnLongPress(true)
        mWrappedAdapter = mRecyclerViewDragDropManager!!.createWrappedAdapter(mWrappedAdapter!!)


        RecyclerView_topic!!.setAdapter(mWrappedAdapter) // requires *wrapped* adapter
        RecyclerView_topic!!.setItemAnimator(animator)

        //For Attach the all manager

        // NOTE:
        // The initialization order is very important! This order determines the priority of touch event handling.
        //
        // priority: TouchActionGuard > Swipe > DragAndDrop
        mRecyclerViewTouchActionGuardManager!!.attachRecyclerView(RecyclerView_topic!!)
        mRecyclerViewSwipeManager!!.attachRecyclerView(RecyclerView_topic!!)

        mRecyclerViewDragDropManager!!.attachRecyclerView(RecyclerView_topic!!)
    }

    private fun initOOBE() {
        val margin = ((getResources().displayMetrics.density * 12) as Number).toInt()

        val centerParams =
            RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        centerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        centerParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
        centerParams.setMargins(0, 0, 0, margin)

        val leftParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        leftParams.setMargins(margin, margin, margin, margin)

        val showcaseViewOnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(view: View?) {
                when (oobeCount) {
                    0 -> {
                        sv!!.setButtonPosition(centerParams)
                        sv!!.setShowcase(CustomViewTarget(RecyclerView_topic as View, 4, 4), true)
                        sv!!.setContentTitle(getString(R.string.oobe_main_topic_list_title))
                        sv!!.setContentText(getString(R.string.oobe_main_topic_list_content))
                    }

                    1 -> {
                        sv!!.setButtonPosition(leftParams)
                        sv!!.setShowcase(ViewTarget(EDT_main_topic_search), true)
                        sv!!.setContentTitle(getString(R.string.oobe_main_search_title))
                        sv!!.setContentText(getString(R.string.oobe_main_search_content))
                    }

                    2 -> {
                        sv!!.setButtonPosition(centerParams)
                        sv!!.setShowcase(ViewTarget(IV_main_setting), true)
                        sv!!.setContentTitle(getString(R.string.oobe_main_adv_setting_title))
                        sv!!.setContentText(getString(R.string.oobe_main_adv_setting_content))
                    }

                    3 -> {
                        sv!!.setButtonPosition(centerParams)
                        sv!!.setTarget(Target.NONE)
                        sv!!.setContentTitle(getString(R.string.oobe_main_mydiary_title))
                        sv!!.setContentText(getString(R.string.oobe_main_mydiary_content))
                        sv!!.setButtonText(getString(R.string.dialog_button_ok))
                    }

                    4 -> sv!!.hide()
                }
                oobeCount++
            }
        }


        val viewTarget: Target = ViewTarget(IV_main_profile_picture)
        sv = ShowcaseView.Builder(this)
            .withMaterialShowcase()
            .setTarget(viewTarget)
            .setContentTitle(getString(R.string.oobe_main_your_name_title))
            .setContentText(getString(R.string.oobe_main_your_name_content)) //.setStyle(R.style.OOBEShowcaseTheme)
            .singleShot(OOBE.MAIN_PAGE.toLong())
            .replaceEndButton(MyDiaryButton(this))
            .setOnClickListener(showcaseViewOnClickListener)
            .build()
        sv!!.setButtonText(getString(R.string.oobe_next_button))
        sv!!.setButtonPosition(leftParams)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun TopicCreated(topicTitle: String?, type: Int, color: Int) {
        dbManager!!.openDB()
        //Create newTopic into List first
        val newTopicId = dbManager!!.insertTopic(topicTitle, type, color)
        //This ITopic is temp object to order
        topicList!!.add(
            0,
            object : ITopic {

                override var title: String?
                    get() = topicTitle
                    set(value) {}
                override val type: Int
                    get() = type
                override val id: Long
                    get() = newTopicId
                override val icon: Int
                    get() = 0
                override var count: Long
                    get() = 0
                    set(value) {}
                override var color: Int
                    get() = color
                    set(value) {}
                override var isPinned: Boolean
                    get() = false
                    set(value) {}
            })
        //Get size
        var orderNumber = topicList!!.size
        //Remove this topic order
        dbManager!!.deleteAllCurrentTopicOrder()
        //sort the topic order
        for (topic in topicList!!) {
            dbManager!!.insertTopicOrder(topic.id, (--orderNumber).toLong())
        }
        loadTopic()
        dbManager!!.closeDB()
        mainTopicAdapter!!.notifyDataSetChanged(true)
        //Clear the filter
        EDT_main_topic_search!!.setText("")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun TopicUpdated(
        position: Int,
        newTopicTitle: String?,
        color: Int,
        topicBgStatus: Int,
        newTopicBgFileName: String?
    ) {
        val dbManager = DBManager(this)
        dbManager.openDB()
        dbManager.updateTopic(
            mainTopicAdapter!!.getList()[position]!!.id,
            newTopicTitle,
            color
        )
        dbManager.closeDB()
        //Update filter list
        mainTopicAdapter!!.getList()[position]?.title = newTopicTitle
        mainTopicAdapter!!.getList()[position]?.color = color
        mainTopicAdapter!!.notifyDataSetChanged(false)

        updateTopicBg(position, topicBgStatus, newTopicBgFileName)
        //Clear the filter
        EDT_main_topic_search!!.setText("")
    }

    override fun updateName() {
        initProfile()
        loadProfilePicture()
    }

    private fun autoClearEditTextFocus() {
        globalLayoutListener = OnGlobalLayoutListener {
            val rect = Rect()
            rootView!!.getWindowVisibleDisplayFrame(rect)
            val heightDiff = rootView!!.getRootView().height - (rect.bottom - rect.top)
            Log.e("Mytest", "height diff:$heightDiff")
            if (heightDiff <= keyboardHeightThreshold) {
                EDT_main_topic_search!!.clearFocus()
            }
        }
        rootView!!.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val locale = MyDiaryApplication.mLocale
        Log.e("Mytest", "main mLocale:$locale")
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }


}