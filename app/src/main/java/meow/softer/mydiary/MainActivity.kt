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
import androidx.appcompat.app.AppCompatActivity
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
import meow.softer.mydiary.main.YourNameDialogFragment
import meow.softer.mydiary.main.YourNameDialogFragment.YourNameCallback
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set status bar
        ChinaPhoneHelper.setStatusBar(this, true)
        themeManager = ThemeManager.getInstance()
        LL_main_profile = findViewById<LinearLayout?>(R.id.LL_main_profile)
        LL_main_profile!!.setOnClickListener(this)

        IV_main_profile_picture = findViewById<ImageView?>(R.id.IV_main_profile_picture)
        TV_main_profile_username = findViewById<TextView?>(R.id.TV_main_profile_username)

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
                    this, mainTopicAdapter!!.list[position].getId(),
                    mainTopicAdapter!!.list[position].getType()
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
                        mainTopicAdapter!!.list[position].getType(), position
                    )
                } catch (e: IOException) {
                    Toast.makeText(this, getString(R.string.topic_topic_bg_fail), Toast.LENGTH_LONG)
                        .show()
                    e.printStackTrace()
                }
            }

            TopicDetailDialogFragment.TOPIC_BG_REVERT_DEFAULT -> {
                val topicBgFile = themeManager!!.getTopicBgSavePathFile(
                    this, mainTopicAdapter!!.list[position].getId(),
                    mainTopicAdapter!!.list[position].getType()
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
            R.id.LL_main_profile -> {
                val yourNameDialogFragment = YourNameDialogFragment()
                yourNameDialogFragment.show(supportFragmentManager, "yourNameDialogFragment")
            }

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
        when (mainTopicAdapter!!.list[position].getType()) {
            ITopic.TYPE_CONTACTS -> dbManager.delAllContactsInTopic(
                mainTopicAdapter!!.list[position].getId()
            )

            ITopic.TYPE_MEMO -> {
                dbManager.delAllMemoInTopic(mainTopicAdapter!!.list[position].getId())
                dbManager.deleteAllCurrentMemoOrder(
                    mainTopicAdapter!!.list[position].getId()
                )
            }

            ITopic.TYPE_DIARY -> {
                //Clear the auto save content
                SPFManager.clearDiaryAutoSave(
                    this,
                    mainTopicAdapter!!.list[position].getId()
                )
                //Because FOREIGN key is not work in this version,
                //so delete diary item first , then delete diary
                val diaryCursor =
                    dbManager.selectDiaryList(mainTopicAdapter!!.list[position].getId())
                var i = 0
                while (i < diaryCursor.count) {
                    dbManager.delAllDiaryItemByDiaryId(diaryCursor.getLong(0))
                    diaryCursor.moveToNext()
                    i++
                }
                diaryCursor.close()
                dbManager.delAllDiaryInTopic(mainTopicAdapter!!.list[position].getId())
            }
        }
        //Delete the dir if it exist.
        try {
            FileUtils.deleteDirectory(
                FileManager(
                    this@MainActivity,
                    mainTopicAdapter!!.list[position].getType(),
                    mainTopicAdapter!!.list[position].getId()
                ).dir
            )
        } catch (e: IOException) {
            //Do nothing if delete fail
            e.printStackTrace()
        }
        dbManager.delTopic(mainTopicAdapter!!.list[position].getId())
        //Don't delete the topic order, it will be refreshed next moving time.
        dbManager.closeDB()
        //Search for remove the topiclist
        for (i in topicList!!.indices) {
            if (topicList!![i].getId() == mainTopicAdapter!!.list[position].getId()) {
                topicList!!.removeAt(i)
                break
            }
        }
        //remove the filter list
        mainTopicAdapter!!.list.removeAt(position)
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
        if (YourNameIs == null || YourNameIs.isEmpty()) {
            YourNameIs = themeManager!!.getThemeUserName(this@MainActivity)
        }
        TV_main_profile_username!!.text = YourNameIs
        LL_main_profile!!.background = themeManager!!.getProfileBgDrawable(this)
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
        IV_main_profile_picture!!.setImageDrawable(themeManager!!.getProfilePictureDrawable(this))
    }

    private fun countTopicContent() {
        dbManager!!.openDB()
        for (i in topicList!!.indices) {
            when (topicList!![i].getType()) {
                ITopic.TYPE_CONTACTS -> topicList!![i].setCount(
                    dbManager!!.getContactsCountByTopicId(topicList!![i].getId()).toLong()
                )

                ITopic.TYPE_DIARY -> topicList!![i].setCount(
                    dbManager!!.getDiaryCountByTopicId(topicList!![i].getId()).toLong()
                )

                ITopic.TYPE_MEMO -> topicList!![i].setCount(
                    dbManager!!.getMemoCountByTopicId(topicList!![i].getId()).toLong()
                )
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
        mainTopicAdapter = MainTopicAdapter(this, topicList, dbManager)
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
                        sv!!.setShowcase(CustomViewTarget(RecyclerView_topic, 4, 4), true)
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
                override fun getTitle(): String? {
                    return topicTitle
                }

                override fun setTitle(title: String?) {
                    //do nothing
                }

                override fun getType(): Int {
                    return type
                }

                override fun getId(): Long {
                    return newTopicId
                }

                override fun getIcon(): Int {
                    return 0
                }

                override fun setCount(count: Long) {
                }

                override fun getCount(): Long {
                    return 0
                }

                override fun getColor(): Int {
                    return color
                }

                override fun setColor(color: Int) {
                    //do nothing
                }

                override fun setPinned(pinned: Boolean) {
                    //do nothing
                }

                override fun isPinned(): Boolean {
                    return false
                }
            })
        //Get size
        var orderNumber = topicList!!.size
        //Remove this topic order
        dbManager!!.deleteAllCurrentTopicOrder()
        //sort the topic order
        for (topic in topicList!!) {
            dbManager!!.insertTopicOrder(topic.getId(), (--orderNumber).toLong())
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
            mainTopicAdapter!!.list[position].getId(),
            newTopicTitle,
            color
        )
        dbManager.closeDB()
        //Update filter list
        mainTopicAdapter!!.list[position].setTitle(newTopicTitle)
        mainTopicAdapter!!.list[position].setColor(color)
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