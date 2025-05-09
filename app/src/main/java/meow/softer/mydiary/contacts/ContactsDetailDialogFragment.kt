package meow.softer.mydiary.contacts

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.R
import meow.softer.mydiary.db.DBManager
import meow.softer.mydiary.shared.ThemeManager.Companion.instance
import meow.softer.mydiary.shared.gui.MyDiaryButton

class ContactsDetailDialogFragment : DialogFragment(), View.OnClickListener {
    interface ContactsDetailCallback {
        fun addContacts()

        fun updateContacts()

        fun deleteContacts()
    }

    /**
     * UI
     */
    private var LL_contacts_detail_top_content: LinearLayout? = null
    private var EDT_contacts_detail_name: EditText? = null
    private var EDT_contacts_detail_phone_number: EditText? = null
    private var But_contacts_detail_delete: MyDiaryButton? = null
    private var But_contacts_detail_cancel: MyDiaryButton? = null
    private var But_contacts_detail_ok: MyDiaryButton? = null

    /**
     * CallBack
     */
    private var callback: ContactsDetailCallback? = null

    /**
     * Contacts Info
     */
    private var contactsId: Long = 0
    private var contactsName: String? = null
    private var contactsPhoneNumber: String? = null
    private var topicId: Long = 0

    //Edit or add contacts
    private var isEditMode = false
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            callback = context as ContactsDetailCallback
        } catch (e: ClassCastException) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // request a window without the title
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.dialog!!.setCanceledOnTouchOutside(false)
        val rootView = inflater.inflate(R.layout.dialog_fragment_contacts_detail, container)

        LL_contacts_detail_top_content =
            rootView.findViewById<LinearLayout>(R.id.LL_contacts_detail_top_content)
        LL_contacts_detail_top_content!!.setBackgroundColor(instance!!.getThemeMainColor(requireActivity()))

        EDT_contacts_detail_name = rootView.findViewById<EditText>(R.id.EDT_contacts_detail_name)
        EDT_contacts_detail_phone_number =
            rootView.findViewById<EditText>(R.id.EDT_contacts_detail_phone_number)


        But_contacts_detail_delete =
            rootView.findViewById<MyDiaryButton>(R.id.But_contacts_detail_delete)
        But_contacts_detail_cancel =
            rootView.findViewById<MyDiaryButton>(R.id.But_contacts_detail_cancel)
        But_contacts_detail_cancel!!.setOnClickListener(this)
        But_contacts_detail_ok = rootView.findViewById<MyDiaryButton>(R.id.But_contacts_detail_ok)
        But_contacts_detail_ok!!.setOnClickListener(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactsId = requireArguments().getLong("contactsId", -1)
        if (contactsId == ADD_NEW_CONTACTS) {
            isEditMode = false
            But_contacts_detail_delete!!.visibility = View.GONE

            topicId = requireArguments().getLong("topicId", -1)
        } else {
            isEditMode = true
            But_contacts_detail_delete!!.visibility = View.VISIBLE
            But_contacts_detail_delete!!.setOnClickListener(this)

            contactsName = requireArguments().getString("contactsName", "")
            contactsPhoneNumber = requireArguments().getString("contactsPhoneNumber", "")
            EDT_contacts_detail_name!!.setText(contactsName)
            EDT_contacts_detail_phone_number!!.setText(contactsPhoneNumber)
        }
    }

    private fun addContacts() {
        val dbManager = DBManager(activity)
        dbManager.openDB()
        dbManager.insertContacts(
            EDT_contacts_detail_name!!.getText().toString(),
            EDT_contacts_detail_phone_number!!.getText().toString(), "", topicId
        )
        dbManager.closeDB()
    }


    private fun updateContacts() {
        val dbManager = DBManager(activity)
        dbManager.openDB()
        dbManager.updateContacts(
            contactsId,
            EDT_contacts_detail_name!!.getText().toString(),
            EDT_contacts_detail_phone_number!!.getText().toString(),
            ""
        )
        dbManager.closeDB()
    }


    private fun deleteContacts() {
        val dbManager = DBManager(activity)
        dbManager.openDB()
        dbManager.delContacts(contactsId)
        dbManager.closeDB()
    }

    private fun buttonOkEvent() {
        if (EDT_contacts_detail_name!!.getText().toString().length > 0
            && EDT_contacts_detail_phone_number!!.getText().toString().length > 0
        ) {
            if (isEditMode) {
                updateContacts()
                callback!!.updateContacts()
            } else {
                addContacts()
                callback!!.addContacts()
            }
            dismiss()
        } else {
            Toast.makeText(
                activity,
                getString(R.string.toast_contacts_empty),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.But_contacts_detail_delete -> {
                deleteContacts()
                callback!!.deleteContacts()
                dismiss()
            }

            R.id.But_contacts_detail_cancel -> dismiss()
            R.id.But_contacts_detail_ok -> buttonOkEvent()
        }
    }

    companion object {
        @JvmField
        val ADD_NEW_CONTACTS: Long = -1


        @JvmStatic
        fun newInstance(
            contactsId: Long,
            contactsName: String?, contactsPhoneNumber: String?, topicId: Long
        ): ContactsDetailDialogFragment {
            val args = Bundle()
            val fragment = ContactsDetailDialogFragment()
            //contactsId = -1 is edit
            args.putLong("contactsId", contactsId)
            args.putString("contactsName", contactsName)
            args.putString("contactsPhoneNumber", contactsPhoneNumber)
            args.putLong("topicId", topicId)
            fragment.setArguments(args)
            return fragment
        }
    }
}
