package meow.softer.mydiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import meow.softer.mydiary.ui.App
import meow.softer.mydiary.ui.home.HomeViewModel

class MainActivity : ComponentActivity() {

//    private var mainTopicAdapter: MainTopicAdapter? = null
//    private var topicList: MutableList<ITopic>? = null
//    private var dbManager: DBManager? = null
//    private var themeManager: ThemeManager? = null
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            App(
                homeViewModel = homeViewModel,
                onTopicClick = {
                    //gotoTopic(it)
                },
                onProfileClick = {
//                    val diaryDialogFragment = DiaryDialogFragment()
//                    diaryDialogFragment.show(
//                        supportFragmentManager,
//                        "diaryDialogFragment"
//                    )
                }
            )
        }

        //set status bar

//        themeManager = ThemeManager.instance
        //rootView = findViewById<View?>(android.R.id.content)

//        topicList = ArrayList<ITopic>()
//        dbManager = DBManager(this@MainActivity)
//
//        initProfile()
//        loadProfilePicture()


        //Init topic adapter
//        dbManager!!.openDB()
//        loadTopic()
//        dbManager!!.closeDB()

    }

//    private fun updateTopicBg(position: Int, topicBgStatus: Int, newTopicBgFileName: String?) {
//        when (topicBgStatus) {
//            TopicDetailDialogFragment.TOPIC_BG_ADD_PHOTO -> {
//                val outputFile = themeManager!!.getTopicBgSavePathFile(
//                    this, mainTopicAdapter!!.getList()[position]!!.id,
//                    mainTopicAdapter!!.getList()[position]!!.type
//                )
//                //Copy file into topic dir
//                try {
//                    if (outputFile.exists()) {
//                        outputFile.delete()
//                    }
//                    val tempFM = FileManager(this, FileManager.TEMP_DIR)
//                    FileUtils.moveFile(
//                        File(
//                            (tempFM.dirAbsolutePath
//                                    + "/" + newTopicBgFileName)
//                        ),
//                        outputFile
//                    )
//                    //Enter the topic
//                    mainTopicAdapter!!.gotoTopic(
//                        mainTopicAdapter!!.getList()[position]!!.type, position
//                    )
//                } catch (e: IOException) {
//                    Toast.makeText(this, getString(R.string.topic_topic_bg_fail), Toast.LENGTH_LONG)
//                        .show()
//                    e.printStackTrace()
//                }
//            }
//
//            TopicDetailDialogFragment.TOPIC_BG_REVERT_DEFAULT -> {
//                val topicBgFile = themeManager!!.getTopicBgSavePathFile(
//                    this, mainTopicAdapter!!.getList()[position]!!.id,
//                    mainTopicAdapter!!.getList()[position]!!.type
//                )
//                //Just delete the file  , the topic's activity will check file for changing the bg
//                if (topicBgFile.exists()) {
//                    topicBgFile.delete()
//                }
//            }
//        }
//    }

//    override fun onStart() {
//        super.onStart()
//        //It should be reload
//        countTopicContent()
//    }

//    override fun onDestroy() {
////        mainTopicAdapter = null
//        super.onDestroy()
//    }

//    private fun initProfile() {
//        var YourNameIs = SPFManager.getYourName(this@MainActivity)
//        if (YourNameIs.isEmpty()) {
//            YourNameIs = themeManager!!.getThemeUserName(this@MainActivity)
//        }
//        //TV_main_profile_username!!.text = YourNameIs
//        homeViewModel.updateUserName(YourNameIs)
//        //LL_main_profile!!.background = themeManager!!.getProfileBgDrawable(this)
//        homeViewModel.updateHeaderBgPic(themeManager!!.getProfileBgPainter(this))
//    }

//    private fun loadTopic() {
//        topicList!!.clear()
//        val topicCursor = dbManager!!.selectTopic()
//        (0..<topicCursor.count).forEach { i ->
//            when (topicCursor.getInt(2)) {
//                ITopic.TYPE_CONTACTS -> topicList!!.add(
//                    Contacts(
//                        topicCursor.getLong(0),
//                        topicCursor.getString(1), topicCursor.getInt(5)
//                    )
//                )
//
//                ITopic.TYPE_DIARY -> topicList!!.add(
//                    Diary(
//                        topicCursor.getLong(0),
//                        topicCursor.getString(1),
//                        topicCursor.getInt(5)
//                    )
//                )
//
//                ITopic.TYPE_MEMO -> topicList!!.add(
//                    Memo(
//                        topicCursor.getLong(0),
//                        topicCursor.getString(1),
//                        topicCursor.getInt(5)
//                    )
//                )
//            }
//            topicCursor.moveToNext()
//        }
//        topicCursor.close()
//        homeViewModel.updateTopicData(topicList!!)
//    }

//    private fun loadProfilePicture() {
//        //IV_main_profile_picture!!.setImageDrawable(themeManager!!.getProfilePictureDrawable(this))
//        homeViewModel.updateUserPic(themeManager!!.getProfilePicPainter(this))
//
//    }

//    private fun countTopicContent() {
//        dbManager!!.openDB()
//        for (i in topicList!!.indices) {
//            when (topicList!![i].type) {
//                ITopic.TYPE_CONTACTS -> topicList!![i].count =
//                    dbManager!!.getContactsCountByTopicId(topicList!![i].id).toLong()
//
//                ITopic.TYPE_DIARY -> topicList!![i].count =
//                    dbManager!!.getDiaryCountByTopicId(topicList!![i].id).toLong()
//
//                ITopic.TYPE_MEMO -> topicList!![i].count =
//                    dbManager!!.getMemoCountByTopicId(topicList!![i].id).toLong()
//            }
//        }
//        dbManager!!.closeDB()
//        //mWrappedAdapter!!.notifyDataSetChanged()
//    }

//    override fun attachBaseContext(base: Context) {
//        super.attachBaseContext(updateBaseContextLocale(base))
//    }
//
//    private fun updateBaseContextLocale(context: Context): Context? {
//        val locale = MyDiaryApplication.mLocale
//        Log.e("Mytest", "main mLocale:$locale")
//        if (locale != null) {
//            Locale.setDefault(locale)
//        }
//        val configuration = context.resources.configuration
//        configuration.setLocale(locale)
//        return context.createConfigurationContext(configuration)
//    }

//    fun gotoTopic(topic: ITopic) {
//        when (topic.type) {
//            ITopic.TYPE_CONTACTS -> {
//                val goContactsPageIntent = Intent(this, ContactsActivity::class.java)
//                goContactsPageIntent.putExtra(
//                    "topicId",
//                    topic.id
//                )
//                goContactsPageIntent.putExtra(
//                    "diaryTitle",
//                    topic.title
//                )
//                this.startActivity(goContactsPageIntent)
//            }
//
//            ITopic.TYPE_DIARY -> {
//                val goEntriesPageIntent = Intent(this, DiaryActivity::class.java)
//                goEntriesPageIntent.putExtra("topicId", topic.id)
//                goEntriesPageIntent.putExtra(
//                    "diaryTitle",
//                    topic.title
//                )
//                goEntriesPageIntent.putExtra("has_entries", true)
//                startActivity(goEntriesPageIntent)
//            }
//
//            ITopic.TYPE_MEMO -> {
//                val goMemoPageIntent = Intent(this, MemoActivity::class.java)
//                goMemoPageIntent.putExtra("topicId", topic.id)
//                goMemoPageIntent.putExtra(
//                    "diaryTitle",
//                    topic.title
//                )
//                startActivity(goMemoPageIntent)
//            }
//        }
//    }
}