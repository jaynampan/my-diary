package meow.softer.mydiary.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import meow.softer.mydiary.contacts.ContactsDetailDialogFragment.Companion.newInstance
import meow.softer.mydiary.contacts.ContactsDetailDialogFragment.ContactsDetailCallback
import meow.softer.mydiary.shared.ThemeManager.Companion.instance
import java.util.Locale

class ContactsAdapter(
    private val mActivity: FragmentActivity,
    private val contactsNamesList: MutableList<ContactsEntity?>,
    private val topicId: Long,
    private val callback: ContactsDetailCallback?
) : RecyclerView.Adapter<ContactsAdapter.TopicViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_contacts_item, parent, false)
        return TopicViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsNamesList.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        if (showHeader(position)) {
            holder.header!!.visibility = View.VISIBLE
            holder.header.text = contactsNamesList[position]!!.sortLetters
        } else {
            holder.header!!.visibility = View.GONE
        }

        holder.tVName.text = contactsNamesList[position]!!.name
        holder.tVPhoneNumber!!.text = contactsNamesList[position]!!.phoneNumber
        holder.itemPosition = position
    }

    fun getPositionForSection(section: Char): Int {
        for (i in 0..<itemCount) {
            val sortStr = contactsNamesList[i]!!.sortLetters
            val firstChar = sortStr!!.uppercase(Locale.getDefault())[0]
            if (firstChar == section) {
                return i
            }
        }
        return -1
    }

    private fun showHeader(position: Int): Boolean {
        return if (position == 0) {
            true
        } else {
            contactsNamesList[position - 1]!!.sortLetters != contactsNamesList[position]!!.sortLetters
        }
    }

    inner class TopicViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener, OnLongClickListener {
        //Header
        val header: TextView? = view.findViewById<TextView?>(R.id.TV_contacts_item_header)

        //Item
        val LL_contacts_item_contant: LinearLayout =
            view.findViewById<LinearLayout>(R.id.LL_contacts_item_contant)
        val iVPhoto: ImageView?
        val tVName: TextView
        val tVPhoneNumber: TextView?
        var itemPosition = 0

        init {

            this.LL_contacts_item_contant.setOnClickListener(this)
            this.LL_contacts_item_contant.setOnLongClickListener(this)
            this.iVPhoto = view.findViewById<ImageView?>(R.id.IV_contacts_photo)
            this.tVName = view.findViewById<TextView>(R.id.TV_contacts_name)
            this.tVPhoneNumber = view.findViewById<TextView?>(R.id.TV_contacts_phone_number)
            this.tVName.setTextColor(instance!!.getThemeMainColor(mActivity))
        }


        override fun onClick(v: View?) {
            val callDialogFragment =
                CallDialogFragment.newInstance(
                    contactsNamesList[itemPosition]!!.name,
                    contactsNamesList[itemPosition]!!.phoneNumber
                )
            callDialogFragment.show(mActivity.supportFragmentManager, "callDialogFragment")
        }

        override fun onLongClick(v: View?): Boolean {
            val contactsDetailDialogFragment =
                newInstance(
                    contactsNamesList[itemPosition]!!.contactsId,
                    contactsNamesList[itemPosition]!!.name,
                    contactsNamesList[itemPosition]!!.phoneNumber,
                    topicId
                )
            contactsDetailDialogFragment.show(
                mActivity.supportFragmentManager,
                "contactsDetailDialogFragment"
            )
            return true
        }
    }
}
