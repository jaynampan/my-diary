package meow.softer.mydiary.contacts

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meow.softer.mydiary.contacts.ContactsDetailDialogFragment.Companion.newInstance
import meow.softer.mydiary.contacts.ContactsDetailDialogFragment.ContactsDetailCallback
import meow.softer.mydiary.data.db.DBManager
import meow.softer.mydiary.shared.MyDiaryApplication
import meow.softer.mydiary.shared.SPFManager.getLocalLanguageCode
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.ThemeManager.Companion.instance
import meow.softer.mydiary.shared.gui.LetterComparator
import meow.softer.mydiary.ui.home.ContactScreen
import meow.softer.mydiary.ui.home.MainViewModel
import java.util.Locale

class ContactsActivity : FragmentActivity(), ContactsDetailCallback {

    private var topicId: Long = 0

    private var EN: String? = null
    private var ZH: String? = null
    private var BN: String? = null

    private var themeManager: ThemeManager? = null


    private var dbManager: DBManager? = null


    //Contacts list from DB
    private var contactList: MutableList<ContactsEntity>? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val contactTitle = viewModel.contactTitle.collectAsStateWithLifecycle().value
            val contactsData = viewModel.contacts.collectAsStateWithLifecycle().value
            val painter = viewModel.contactBackgroundPainter.collectAsStateWithLifecycle().value
            ContactScreen(
                headerName = contactTitle,
                data = contactsData,
                onAddContact = {
                    val contactsDetailDialogFragment =
                        newInstance(
                            ContactsDetailDialogFragment.ADD_NEW_CONTACTS,
                            "", "", topicId
                        )
                    contactsDetailDialogFragment.show(
                        supportFragmentManager,
                        "contactsDetailDialogFragment"
                    )
                },

                backgroundPainter = painter,
                onClickContact = {
                    val callDialogFragment =
                        CallDialogFragment.newInstance(
                            it.name,
                            it.number
                        )
                    callDialogFragment.show(supportFragmentManager, "callDialogFragment")
                },
                onLongPressContact = {
                    val contactsDetailDialogFragment =
                        newInstance(
                            it.id,
                            it.name,
                            it.number,
                            topicId
                        )
                    contactsDetailDialogFragment.show(
                        supportFragmentManager,
                        "contactsDetailDialogFragment"
                    )
                },
            )
            //todo: update background
        }

        themeManager = instance
        initLanguageStr()

        topicId = intent.getLongExtra("topicId", -1)
        if (topicId == -1L) {
            finish()
        }

        val contactsBgDrawable = themeManager!!.getContactsBgDrawable(this, topicId)
        val bgImageBitmap = contactsBgDrawable?.toBitmap()?.asImageBitmap()
        bgImageBitmap?.let {
            viewModel.updateContactBackground(
                BitmapPainter(it)
            )
        }


        var diaryTitle = intent.getStringExtra("diaryTitle")
        if (diaryTitle == null) {
            diaryTitle = "Contacts"
        }

        viewModel.updateContactTitle(diaryTitle)

        contactList = ArrayList<ContactsEntity>()
        dbManager = DBManager(this@ContactsActivity)

        loadContacts()
    }

    private fun initLanguageStr() {
        EN = Locale.ENGLISH.language
        ZH = Locale.CHINA.language
        BN = Locale("bn", "").language
    }

    private fun loadContacts() {
        contactList!!.clear()
        dbManager!!.openDB()
        val contactsCursor = dbManager!!.selectContacts(topicId)
        for (i in 0..<contactsCursor.count) {
            contactList!!.add(
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
        for (contactsEntity in contactList!!) {
            val sortString = contactsEntity.name!!.substring(0, 1).uppercase(Locale.getDefault())
            if (checkLanguage() == ZH) {
                sortContactsCN(contactsEntity, sortString)
            } else {
                sortContactsEN(contactsEntity, sortString)
            }
        }
        contactList!!.sortWith(LetterComparator())
        viewModel.updateContactData(contactList)
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

    override fun addContacts() {
        loadContacts()
    }

    override fun updateContacts() {
        loadContacts()
    }

    override fun deleteContacts() {
        loadContacts()
    }


    /**
     * This code is from array
     * System = 0
     * English = 1
     * 中文 = 2
     * Bangla = 3
     */
    private fun checkLanguage(): String? {
        val language: String? = when (getLocalLanguageCode(this)) {
            1 -> EN
            2 ->                 // CHINESE;
                ZH

            3 ->                 //BANGLA
                BN

            else -> resources.configuration.locale.language +
                    "-" + resources.configuration.locale.country
        }
        return language
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val locale = MyDiaryApplication.mLocale
        Log.e("Mytest", "contacts mLocale:$locale")
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
