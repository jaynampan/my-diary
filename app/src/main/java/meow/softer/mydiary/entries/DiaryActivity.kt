package meow.softer.mydiary.entries

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import info.hoang8f.android.segmented.SegmentedGroup
import meow.softer.mydiary.R
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.entries.calendar.CalendarFragment
import meow.softer.mydiary.entries.diary.DiaryFragment
import meow.softer.mydiary.entries.diary.item.IDiaryRow
import meow.softer.mydiary.entries.entry.EntriesEntity
import meow.softer.mydiary.entries.entry.EntriesFragment
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper
import java.util.Date
import java.util.Locale
import kotlin.math.min

//import android.support.v4.app.FragmentPagerAdapter;
class DiaryActivity : FragmentActivity(), RadioGroup.OnCheckedChangeListener {
    /**
     * Public data
     */
    var topicId: Long = 0
        private set
    private var hasEntries = false

    /**
     * UI
     */
    private var LL_diary_topbar_content: LinearLayout? = null
    private var ViewPager_diary_content: ViewPager? = null
    private var TV_diary_topbar_title: TextView? = null
    private var SG_diary_topbar: SegmentedGroup? = null
    private var But_diary_topbar_entries: RadioButton? = null
    private var But_diary_topbar_calendar: RadioButton? = null
    private var But_diary_topbar_diary: RadioButton? = null

    /**
     * View pager
     */
    private var mPagerAdapter: ScreenSlidePagerAdapter? = null

    /**
     * Google API
     */
    //private GoogleApiClient mGoogleApiClient;
    /**
     * The diary list for every fragment
     */
    val entriesList: MutableList<EntriesEntity?> = ArrayList<EntriesEntity?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)
        //For set status bar
        ChinaPhoneHelper.setStatusBar(this, true)
        setStatusBarBgColor()

        topicId = intent.getLongExtra("topicId", -1)
        hasEntries = intent.getBooleanExtra("has_entries", true)
        if (topicId == -1L) {
            finish()
        }
        /**
         * init UI
         */
        LL_diary_topbar_content = findViewById<LinearLayout?>(R.id.LL_diary_topbar_content)
        SG_diary_topbar = findViewById<SegmentedGroup>(R.id.SG_diary_topbar)
        SG_diary_topbar!!.setOnCheckedChangeListener(this)
        SG_diary_topbar!!.setTintColor(ThemeManager.getInstance().getThemeDarkColor(this))
        But_diary_topbar_entries = findViewById<RadioButton>(R.id.But_diary_topbar_entries)
        But_diary_topbar_calendar = findViewById<RadioButton>(R.id.But_diary_topbar_calendar)
        But_diary_topbar_diary = findViewById<RadioButton>(R.id.But_diary_topbar_diary)
        TV_diary_topbar_title = findViewById<TextView>(R.id.TV_diary_topbar_title)
        TV_diary_topbar_title!!.setTextColor(ThemeManager.getInstance().getThemeDarkColor(this))

        var diaryTitle = intent.getStringExtra("diaryTitle")
        if (diaryTitle == null) {
            diaryTitle = "Diary"
        }
        TV_diary_topbar_title!!.text = diaryTitle
        //After SegmentedGroup was created
        initViewPager()

        //initGoogleAPi();

        //Load the all entries from db.
        loadEntries()
    }

    fun loadEntries() {
        entriesList.clear()
        val dbManager = DBManager(this)
        dbManager.openDB()
        //Select diary info
        val diaryCursor = dbManager.selectDiaryList(this.topicId)
        for (i in 0..<diaryCursor.count) {
            //get diary info
            var title = diaryCursor.getString(2)
            if ("" == title) {
                title = getString(R.string.diary_no_title)
            }
            val entity = EntriesEntity(
                diaryCursor.getLong(0),
                Date(diaryCursor.getLong(1)),
                title.substring(
                    0,
                    min(MAX_TEXT_LENGTH.toDouble(), title.length.toDouble()).toInt()
                ),
                diaryCursor.getInt(4), diaryCursor.getInt(3),
                diaryCursor.getInt(5) > 0
            )

            //select first diary content
            val diaryContentCursor = dbManager.selectDiaryContentByDiaryId(entity.id)
            if (diaryContentCursor != null && diaryContentCursor.count > 0) {
                var summary = ""
                //Check content Type
                if (diaryContentCursor.getInt(1) == IDiaryRow.TYPE_PHOTO) {
                    summary = getString(R.string.entries_summary_photo)
                } else if (diaryContentCursor.getInt(1) == IDiaryRow.TYPE_TEXT) {
                    summary = diaryContentCursor.getString(3)
                        .substring(
                            0,
                            min(
                                MAX_TEXT_LENGTH.toDouble(),
                                diaryContentCursor.getString(3).length.toDouble()
                            ).toInt()
                        )
                }
                entity.summary = summary
                diaryContentCursor.close()
            }
            //Add entity
            entriesList.add(entity)
            diaryCursor.moveToNext()
        }
        diaryCursor.close()
        dbManager.closeDB()
    }

    /**
     * Init Viewpager
     */
    private fun initViewPager() {
        ViewPager_diary_content = findViewById<ViewPager>(R.id.ViewPager_diary_content)
        //Make viewpager load one fragment every time.
        ViewPager_diary_content!!.setOffscreenPageLimit(2)
        mPagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        ViewPager_diary_content!!.setAdapter(mPagerAdapter)
        ViewPager_diary_content!!.addOnPageChangeListener(onPageChangeListener)
        ViewPager_diary_content!!.background =
            ThemeManager.getInstance().getEntriesBgDrawable(this, this.topicId)
        if (!hasEntries) {
            ViewPager_diary_content!!.setCurrentItem(2)
            //Set Default Checked Item
            But_diary_topbar_diary!!.setChecked(true)
        } else {
            //Set Default Checked Item
            But_diary_topbar_entries!!.setChecked(true)
        }
    }

    fun gotoPage(position: Int) {
        ViewPager_diary_content!!.setCurrentItem(position)
    }

    fun callEntriesGotoDiaryPosition(position: Int) {
        val entriesFragment = (mPagerAdapter!!.getRegisteredFragment(0) as EntriesFragment?)
        if (entriesFragment != null) {
            gotoPage(0)
            entriesFragment.gotoDiaryPosition(position)
        }
    }

    fun callEntriesListRefresh() {
        val entriesFragment = (mPagerAdapter!!.getRegisteredFragment(0) as EntriesFragment?)
        entriesFragment?.updateEntriesData()
    }

    fun callCalendarRefresh() {
        val calendarFragment = (mPagerAdapter!!.getRegisteredFragment(1) as CalendarFragment?)
        calendarFragment?.refreshCalendar()
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.But_diary_topbar_entries -> ViewPager_diary_content!!.setCurrentItem(0)
            R.id.But_diary_topbar_calendar -> ViewPager_diary_content!!.setCurrentItem(1)
            R.id.But_diary_topbar_diary -> ViewPager_diary_content!!.setCurrentItem(2)
        }
    }

    private val onPageChangeListener: OnPageChangeListener = object : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            when (position) {
                1 -> But_diary_topbar_calendar!!.setChecked(true)
                2 -> But_diary_topbar_diary!!.setChecked(true)
                else -> But_diary_topbar_entries!!.setChecked(true)
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val registeredFragments = SparseArray<Fragment?>()

        override fun getItem(position: Int): BaseDiaryFragment {
            val fragment: BaseDiaryFragment
            fragment = when (position) {
                1 -> CalendarFragment()
                2 -> DiaryFragment()
                else -> EntriesFragment()
            }
            return fragment
        }

        override fun getCount(): Int {
            return 3
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            registeredFragments.remove(position)
            super.destroyItem(container, position, `object`)
        }

        fun getRegisteredFragment(position: Int): Fragment? {
            return registeredFragments.get(position)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val locale = MyDiaryApplication.mLocale
        Log.e("Mytest", "init mLocale:$locale")
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun setStatusBarBgColor() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE
    }

    companion object {
        private const val MAX_TEXT_LENGTH = 18
    }
}
