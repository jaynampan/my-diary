package meow.softer.mydiary.contacts

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import meow.softer.mydiary.contacts.ContactsDetailDialogFragment.Companion.newInstance
import meow.softer.mydiary.contacts.ContactsDetailDialogFragment.ContactsDetailCallback
import meow.softer.mydiary.contacts.LetterSortLayout.OnTouchingLetterChangedListener
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.SPFManager.getLocalLanguageCode
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.ThemeManager.Companion.instance
import meow.softer.mydiary.shared.gui.LetterComparator
import meow.softer.mydiary.shared.statusbar.ChinaPhoneHelper.Companion.setStatusBar
import java.util.Locale

class ContactsActivity : FragmentActivity(), View.OnClickListener, ContactsDetailCallback,
    OnTouchingLetterChangedListener {
    /**
     * getId
     */
    private var topicId: Long = 0

    /**
     * Sort
     */
    private var EN: String? = null
    private var ZH: String? = null
    private var BN: String? = null

    /**
     * UI
     */
    private var themeManager: ThemeManager? = null
    private var RL_contacts_content: RelativeLayout? = null
    private var IV_contacts_title: TextView? = null
    private var EDT_main_contacts_search: EditText? = null
    private var STL_contacts: LetterSortLayout? = null
    private var IV_contacts_add: ImageView? = null
    private var TV_contact_short_sort: TextView? = null

    /**
     * DB
     */
    private var dbManager: DBManager? = null

    /**
     * RecyclerView
     */
    private var RecyclerView_contacts: RecyclerView? = null
    private var contactsAdapter: ContactsAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    //Contacts list from DB
    private var contactsNamesList: MutableList<ContactsEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
         
        setStatusBar(this, true)
        setStatusBarBgColor()

        themeManager = instance
        initLanguageStr()

        topicId = intent.getLongExtra("topicId", -1)
        if (topicId == -1L) {
            finish()
        }
        /**
         * init UI
         */
        RL_contacts_content = findViewById<RelativeLayout>(R.id.RL_contacts_content)
        RL_contacts_content!!.background = themeManager!!.getContactsBgDrawable(this, topicId)

        TV_contact_short_sort = findViewById<TextView>(R.id.TV_contact_short_sort)
        TV_contact_short_sort!!.setBackgroundColor(themeManager!!.getThemeDarkColor(this@ContactsActivity))

        STL_contacts = findViewById<LetterSortLayout>(R.id.STL_contacts)
        STL_contacts!!.setSortTextView(TV_contact_short_sort)
        STL_contacts!!.setOnTouchingLetterChangedListener(this)

        EDT_main_contacts_search = findViewById<EditText>(R.id.EDT_main_contacts_search)
        IV_contacts_add = findViewById<ImageView>(R.id.IV_contacts_add)
        IV_contacts_add!!.setOnClickListener(this)

        IV_contacts_title = findViewById<TextView>(R.id.IV_contacts_title)
        var diaryTitle = intent.getStringExtra("diaryTitle")
        if (diaryTitle == null) {
            diaryTitle = "Contacts"
        }
        IV_contacts_title!!.text = diaryTitle


        /**
         * init RecyclerVie
         */
        STL_contacts = findViewById<LetterSortLayout>(R.id.STL_contacts)
        RecyclerView_contacts = findViewById<RecyclerView>(R.id.RecyclerView_contacts)
        contactsNamesList = ArrayList<ContactsEntity>()
        dbManager = DBManager(this@ContactsActivity)

        initTopbar()
        loadContacts()
        initTopicAdapter()
    }

    private fun initLanguageStr() {
        EN = Locale.ENGLISH.language
        ZH = Locale.CHINA.language
        BN = Locale("bn", "").language
    }

    private fun initTopbar() {
        EDT_main_contacts_search!!.background.setColorFilter(
            themeManager!!.getThemeMainColor(this),
            PorterDuff.Mode.SRC_ATOP
        )
        IV_contacts_title!!.setTextColor(themeManager!!.getThemeMainColor(this))
        IV_contacts_add!!.setColorFilter(themeManager!!.getThemeMainColor(this))
    }

    private fun loadContacts() {
        contactsNamesList!!.clear()
        dbManager!!.openDB()
        val contactsCursor = dbManager!!.selectContacts(topicId)
        for (i in 0..<contactsCursor.count) {
            contactsNamesList!!.add(
                ContactsEntity(
                    contactsCursor.getLong(0), contactsCursor.getString(1),
                    contactsCursor.getString(2), contactsCursor.getString(3)
                )
            )
            contactsCursor.moveToNext()
        }
        contactsCursor.close()
        dbManager!!.closeDB()
        sortContacts()
    }

    private fun sortContacts() {
        for (contactsEntity in contactsNamesList!!) {
            val sortString = contactsEntity.name!!.substring(0, 1).uppercase(Locale.getDefault())
            if (checkLanguage() == ZH) {
                sortContactsCN(contactsEntity, sortString)
            } else {
                sortContactsEN(contactsEntity, sortString)
            }
        }
        contactsNamesList!!.sortWith(LetterComparator())
    }

    private fun sortContactsCN(contactsEntity: ContactsEntity, sortString: String): String {
        if (sortString.matches("[\\u4E00-\\u9FA5]".toRegex())) {
            //TODO:String[] arr = PinyinHelper.toHanyuPinyinStringArray(sortString.trim().charAt(0));
            //sortString = arr[0].substring(0, 1).toUpperCase();
        }
        if (sortString.matches("[A-Z]".toRegex())) {
            contactsEntity.sortLetters = sortString.uppercase(Locale.getDefault())
        } else {
            contactsEntity.sortLetters = "#"
        }
        return sortString
    }

    private fun sortContactsEN(contactsEntity: ContactsEntity, sortString: String) {
        if (sortString.matches("[A-Z]".toRegex())) {
            contactsEntity.sortLetters = sortString.uppercase(Locale.getDefault())
        } else {
            contactsEntity.sortLetters = "#"
        }
    }

    private fun initTopicAdapter() {
        //Init topic adapter
        layoutManager = LinearLayoutManager(this)
        RecyclerView_contacts!!.setLayoutManager(layoutManager)
        RecyclerView_contacts!!.setHasFixedSize(true)
        contactsAdapter = ContactsAdapter(this@ContactsActivity,
            contactsNamesList as MutableList<ContactsEntity?>, topicId, this)
        RecyclerView_contacts!!.setAdapter(contactsAdapter)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.IV_contacts_add -> {
                val contactsDetailDialogFragment =
                    newInstance(
                        ContactsDetailDialogFragment.ADD_NEW_CONTACTS,
                        "", "", topicId
                    )
                contactsDetailDialogFragment.show(
                    supportFragmentManager,
                    "contactsDetailDialogFragment"
                )
            }
        }
    }

    override fun addContacts() {
        loadContacts()
        contactsAdapter!!.notifyDataSetChanged()
    }

    override fun updateContacts() {
        loadContacts()
        contactsAdapter!!.notifyDataSetChanged()
    }

    override fun deleteContacts() {
        loadContacts()
        contactsAdapter!!.notifyDataSetChanged()
    }

    override fun onTouchingLetterChanged(s: String?) {
        val position = contactsAdapter!!.getPositionForSection(s!!.get(0))
        if (position != -1) {
            RecyclerView_contacts!!.layoutManager!!.scrollToPosition(position)
        }
    }

    /**
     * This code is from array
     * System = 0
     * English = 1
     * 中文 = 2
     * Bangla = 3
     */
    private fun checkLanguage(): String? {
        val language: String?
        when (getLocalLanguageCode(this)) {
            1 -> language = EN
            2 ->                 // CHINESE;
                language = ZH

            3 ->                 //BANGLA
                language = BN

            else -> language = resources.configuration.locale.language +
                    "-" + resources.configuration.locale.country
        }
        return language
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val locale = MyDiaryApplication.mLocale
        Log.e("Mytest", "contacts mLocale:" + locale)
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
