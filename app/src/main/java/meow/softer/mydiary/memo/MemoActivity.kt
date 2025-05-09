package meow.softer.mydiary.memo

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.memo.EditMemoDialogFragment.Companion.newInstance
import meow.softer.mydiary.memo.EditMemoDialogFragment.MemoCallback
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.ViewTools
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper
import java.util.Locale

class MemoActivity : FragmentActivity(), View.OnClickListener, MemoCallback, OnStartDragListener {
    /**
     * getId
     */
    private var topicId: Long = 0

    /**
     * UI
     */
    private var RL_memo_topbar_content: RelativeLayout? = null
    private var TV_memo_topbar_title: TextView? = null
    private var IV_memo_edit: ImageView? = null
    private var rootView: View? = null
    private var TV_memo_item_add: TextView? = null

    /**
     * DB
     */
    private var dbManager: DBManager? = null

    /**
     * RecyclerView
     */
    private var RL_memo_content_bg: RelativeLayout? = null
    private var RecyclerView_memo: RecyclerView? = null
    private var memoAdapter: MemoAdapter? = null
    private var memoList: MutableList<MemoEntity>? = null
    private var touchHelper: ItemTouchHelper? = null

    override fun onBackPressed() {
        if (memoAdapter!!.isEditMode) {
            setEditModeUI(memoAdapter!!.isEditMode)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memo)

        //For set status bar
        ChinaPhoneHelper.setStatusBar(this, true)
        setStatusBarBgColor()
        topicId = intent.getLongExtra("topicId", -1)
        if (topicId == -1L) {
            finish()
        }
        /**
         * init UI
         */
        RL_memo_topbar_content = findViewById<RelativeLayout>(R.id.RL_memo_topbar_content)
        RL_memo_topbar_content!!.setBackgroundColor(
            ThemeManager.getInstance().getThemeMainColor(this)
        )

        RL_memo_content_bg = findViewById<RelativeLayout>(R.id.RL_memo_content_bg)
        RL_memo_content_bg!!.background = ThemeManager.getInstance().getMemoBgDrawable(this, topicId)

        TV_memo_topbar_title = findViewById<TextView>(R.id.TV_memo_topbar_title)
        IV_memo_edit = findViewById<ImageView>(R.id.IV_memo_edit)
        IV_memo_edit!!.setOnClickListener(this)
        var diaryTitle = intent.getStringExtra("diaryTitle")
        if (diaryTitle == null) {
            diaryTitle = "Memo"
        }
        TV_memo_topbar_title!!.text = diaryTitle

        RecyclerView_memo = findViewById<RecyclerView>(R.id.RecyclerView_memo)
        rootView = findViewById<View>(R.id.Layout_memo_item_add)
        TV_memo_item_add = rootView!!.findViewById<TextView>(R.id.TV_memo_item_add)
        TV_memo_item_add!!.setTextColor(ThemeManager.getInstance().getThemeDarkColor(this))
        TV_memo_item_add!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val editMemoDialogFragment = newInstance(
                    topicId, -1, true, ""
                )

                editMemoDialogFragment.show(supportFragmentManager, "editMemoDialogFragment")
            }
        })
        memoList = ArrayList<MemoEntity>()
        dbManager = DBManager(this@MemoActivity)

        loadMemo(true)
        initTopicAdapter()
    }

    private fun loadMemo(openDB: Boolean) {
        memoList!!.clear()
        if (openDB) {
            dbManager!!.openDB()
        }
        val memoCursor = dbManager!!.selectMemoAndMemoOrder(topicId)
        for (i in 0..<memoCursor.count) {
            memoList!!.add(
                MemoEntity(
                    memoCursor.getLong(0),
                    memoCursor.getString(2),
                    memoCursor.getInt(3) > 0
                )
            )
            memoCursor.moveToNext()
        }
        memoCursor.close()
        if (openDB) {
            dbManager!!.closeDB()
        }
    }

    private fun initTopicAdapter() {
        //Init topic adapter
        val lmr = LinearLayoutManager(this)
        RecyclerView_memo!!.setLayoutManager(lmr)
        RecyclerView_memo!!.setHasFixedSize(true)
        memoAdapter = MemoAdapter(
            this@MemoActivity, topicId, memoList!!, dbManager!!,
            this, this
        )
        RecyclerView_memo!!.setAdapter(memoAdapter)
        //Set ItemTouchHelper
        val callback: ItemTouchHelper.Callback =
            MemoItemTouchHelperCallback(memoAdapter!!)
        touchHelper = ItemTouchHelper(callback)
        touchHelper!!.attachToRecyclerView(RecyclerView_memo)
    }

    fun setEditModeUI(isEditMode: Boolean) {
        if (isEditMode) {
            //Cancel edit
            IV_memo_edit!!.setImageDrawable(
                ViewTools.getDrawable(
                    this@MemoActivity,
                    R.drawable.ic_mode_edit_white_24dp
                )
            )
            rootView!!.visibility = View.GONE
        } else {
            //Start edit
            IV_memo_edit!!.setImageDrawable(
                ViewTools.getDrawable(
                    this@MemoActivity,
                    R.drawable.ic_mode_edit_cancel_white_24dp
                )
            )
            rootView!!.visibility = View.VISIBLE
        }
        memoAdapter!!.isEditMode = !isEditMode
        memoAdapter!!.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.IV_memo_edit -> setEditModeUI(memoAdapter!!.isEditMode)
        }
    }

    override fun addMemo(memoContent: String?) {
        dbManager!!.openDB()

        //Create newMemoEntity into List first
        val newMemoEntity = MemoEntity(
            dbManager!!.insertMemo(memoContent, false, topicId),
            memoContent, false
        )
        memoList!!.add(0, newMemoEntity)
        //Get size
        var orderNumber = memoList!!.size
        //Remove this topic's all memo order
        dbManager!!.deleteAllCurrentMemoOrder(topicId)
        //sort the memo order
        for (memoEntity in memoList!!) {
            dbManager!!.insertMemoOrder(topicId, memoEntity.memoId, (--orderNumber).toLong())
        }
        //Load again
        loadMemo(false)
        dbManager!!.closeDB()
        memoAdapter!!.notifyDataSetChanged()
    }

    override fun updateMemo() {
        loadMemo(true)
        memoAdapter!!.notifyDataSetChanged()
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper!!.startDrag(viewHolder)
    }

    //fix the language bug
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val locale = MyDiaryApplication.mLocale
        Log.e("Mytest", "memo mLocale:$locale")
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun setStatusBarBgColor() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.WHITE
    }
}
