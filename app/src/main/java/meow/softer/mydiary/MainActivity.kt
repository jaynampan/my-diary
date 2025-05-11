package meow.softer.mydiary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import meow.softer.mydiary.contacts.ContactsActivity
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.main.DiaryDialogFragment
import meow.softer.mydiary.main.DiaryDialogFragment.YourNameCallback
import meow.softer.mydiary.main.MainSettingDialogFragment
import meow.softer.mydiary.main.MainTopicAdapter
import meow.softer.mydiary.main.ReleaseNoteDialogFragment
import meow.softer.mydiary.main.TopicDeleteDialogFragment
import meow.softer.mydiary.main.TopicDetailDialogFragment
import meow.softer.mydiary.main.TopicDetailDialogFragment.TopicCreatedCallback
import meow.softer.mydiary.main.topic.Contacts
import meow.softer.mydiary.main.topic.Diary
import meow.softer.mydiary.main.topic.ITopic
import meow.softer.mydiary.main.topic.Memo
import meow.softer.mydiary.memo.MemoActivity
import meow.softer.mydiary.shared.FileManager
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.SPFManager
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.ui.home.MainViewModel
import meow.softer.mydiary.ui.navigation.DiaryNav
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MainActivity : FragmentActivity(), TopicCreatedCallback,
    YourNameCallback,
    TopicDeleteDialogFragment.DeleteCallback {
    private var isExit: Boolean

    init {
        // Back button event
        isExit = false

    }


    private var mainTopicAdapter: MainTopicAdapter? = null
    private var topicList: MutableList<ITopic>? = null

    /*
     * DB
     */
    private var dbManager: DBManager? = null


    private val backTimer = Timer()

    /*
     * UI
     */
    private var themeManager: ThemeManager? = null
    private var EDT_main_topic_search: EditText? = null

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiaryNav(
                mainViewModel = mainViewModel,
                onTopicClick = { gotoTopic(it) },
                onSettingClick = {
                    val mainSettingDialogFragment = MainSettingDialogFragment()
                    mainSettingDialogFragment.show(
                        supportFragmentManager,
                        "mainSettingDialogFragment"
                    )
                },
                onProfileClick = {
                    val diaryDialogFragment = DiaryDialogFragment()
                    diaryDialogFragment.show(
                        supportFragmentManager,
                        "diaryDialogFragment"
                    )
                }
            )
        }

        //set status bar

        themeManager = ThemeManager.instance
        //rootView = findViewById<View?>(android.R.id.content)

        topicList = ArrayList<ITopic>()
        dbManager = DBManager(this@MainActivity)

        initProfile()
        //initBottomBar()
        //initTopicAdapter()
        loadProfilePicture()


        //Init topic adapter
        dbManager!!.openDB()
        loadTopic()
        dbManager!!.closeDB()

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
        super.onPause()
    }

    override fun onDestroy() {
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

    private fun loadTopic() {
        topicList!!.clear()
        val topicCursor = dbManager!!.selectTopic()
        (0..<topicCursor.count).forEach { i ->
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
        mainViewModel.updateTopicData(topicList!!)
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
        //mWrappedAdapter!!.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun TopicCreated(topicTitle: String?, type: Int, color: Int) {
        dbManager!!.openDB()
        //Create newTopic into List first
        val newTopicId = dbManager!!.insertTopic(topicTitle, type, color)
        //This ITopic is temp object to order
        topicList!!.add(
            0,
            element = object : ITopic {

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

    }

    override fun updateName() {
        initProfile()
        loadProfilePicture()
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

    fun gotoTopic(topic: ITopic) {
        when (topic.type) {
            ITopic.TYPE_CONTACTS -> {
                val goContactsPageIntent = Intent(this, ContactsActivity::class.java)
                goContactsPageIntent.putExtra(
                    "topicId",
                    topic.id
                )
                goContactsPageIntent.putExtra(
                    "diaryTitle",
                    topic.title
                )
                this.startActivity(goContactsPageIntent)
            }

            ITopic.TYPE_DIARY -> {
                val goEntriesPageIntent = Intent(this, DiaryActivity::class.java)
                goEntriesPageIntent.putExtra("topicId", topic.id)
                goEntriesPageIntent.putExtra(
                    "diaryTitle",
                    topic.title
                )
                goEntriesPageIntent.putExtra("has_entries", true)
                startActivity(goEntriesPageIntent)
            }

            ITopic.TYPE_MEMO -> {
                val goMemoPageIntent = Intent(this, MemoActivity::class.java)
                goMemoPageIntent.putExtra("topicId", topic.id)
                goMemoPageIntent.putExtra(
                    "diaryTitle",
                    topic.title
                )
                startActivity(goMemoPageIntent)
            }
        }
    }

}