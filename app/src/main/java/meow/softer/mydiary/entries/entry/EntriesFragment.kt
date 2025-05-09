package meow.softer.mydiary.entries.entry

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import meow.softer.mydiary.entries.BaseDiaryFragment
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.entries.entry.DiaryViewerDialogFragment.DiaryViewerCallback
import meow.softer.mydiary.entries.photo.PhotoOverviewActivity
import meow.softer.mydiary.shared.ThemeManager
import meow.softer.mydiary.shared.ViewTools

class EntriesFragment : BaseDiaryFragment(), DiaryViewerCallback, View.OnClickListener {
    /**
     * UI
     */
    private var TV_entries_count: TextView? = null
    private var RL_entries_edit_bar: RelativeLayout? = null
    private var TV_entries_edit_msg: TextView? = null
    private var IV_entries_edit: ImageView? = null
    private var IV_entries_photo: ImageView? = null

    /**
     * RecyclerView
     */
    private var RecyclerView_entries: RecyclerView? = null
    private var entriesAdapter: EntriesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_entries, container, false)

        IV_entries_edit = rootView.findViewById<ImageView>(R.id.IV_entries_edit)
        IV_entries_edit!!.setOnClickListener(this)
        IV_entries_photo = rootView.findViewById<ImageView>(R.id.IV_entries_photo)
        IV_entries_photo!!.setOnClickListener(this)
        TV_entries_edit_msg = rootView.findViewById<TextView>(R.id.TV_entries_edit_msg)
        TV_entries_edit_msg!!.setTextColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )

        RecyclerView_entries = rootView.findViewById<RecyclerView>(R.id.RecyclerView_entries)
        TV_entries_count = rootView.findViewById<TextView>(R.id.TV_entries_count)
        RL_entries_edit_bar = rootView.findViewById<RelativeLayout>(R.id.RL_entries_edit_bar)
        RL_entries_edit_bar!!.setBackgroundColor(
            ThemeManager.instance!!.getThemeMainColor(requireContext())
        )
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initRecyclerView()
        countEntries()
    }

    override fun onStart() {
        super.onStart()
    }


    private fun initRecyclerView() {
        val lmr = LinearLayoutManager(activity)
        RecyclerView_entries!!.setLayoutManager(lmr)
        entriesAdapter = EntriesAdapter(this@EntriesFragment, entriesList!!)
        RecyclerView_entries!!.setAdapter(entriesAdapter)
        //true for close all view
        setEditModeUI(true)
    }

    private fun countEntries() {
        TV_entries_count!!.text = resources.getQuantityString(
            R.plurals.entries_count,
            entriesList!!.size, entriesList?.size
        )
    }

    fun setEditModeUI(isEditMode: Boolean) {
        if (isEditMode) {
            entriesAdapter!!.isEditMode = false
            TV_entries_edit_msg!!.visibility = View.GONE
            IV_entries_edit!!.setImageDrawable(
                ViewTools.getDrawable(
                    requireContext(),
                    R.drawable.ic_mode_edit_white_24dp
                )
            )
        } else {
            entriesAdapter!!.isEditMode = true
            TV_entries_edit_msg!!.visibility = View.VISIBLE
            IV_entries_edit!!.setImageDrawable(
                ViewTools.getDrawable(
                    requireContext(),
                    R.drawable.ic_mode_edit_cancel_white_24dp
                )
            )
        }
    }

    fun gotoDiaryPosition(position: Int) {
        RecyclerView_entries!!.scrollToPosition(position)
    }

    fun updateEntriesData() {
        updateEntriesList()
        entriesAdapter!!.notifyDataSetChanged()
        countEntries()
        (activity as DiaryActivity).callCalendarRefresh()
    }

    override fun deleteDiary() {
        updateEntriesData()
    }

    override fun updateDiary() {
        updateEntriesData()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.IV_entries_edit -> setEditModeUI(entriesAdapter!!.isEditMode)
            R.id.IV_entries_photo -> {
                val gotoPhotoOverviewIntent =
                    Intent(activity, PhotoOverviewActivity::class.java)
                gotoPhotoOverviewIntent.putExtra(
                    PhotoOverviewActivity.PHOTO_OVERVIEW_TOPIC_ID,
                    topicId
                )
                requireActivity().startActivity(gotoPhotoOverviewIntent)
            }
        }
    }
}
