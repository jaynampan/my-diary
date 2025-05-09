package meow.softer.mydiary.entries.entry

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import meow.softer.mydiary.R
import meow.softer.mydiary.entries.diary.DiaryInfoHelper.getMoodResourceId
import meow.softer.mydiary.entries.diary.DiaryInfoHelper.getWeatherResourceId
import meow.softer.mydiary.entries.entry.DiaryViewerDialogFragment.Companion.newInstance
import meow.softer.mydiary.entries.entry.EntriesAdapter.EntriesViewHolder
import meow.softer.mydiary.shared.ThemeManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar

class EntriesAdapter(
    private val mFragment: EntriesFragment,
    private val entriesList: MutableList<EntriesEntity?>
) : RecyclerView.Adapter<EntriesViewHolder?>() {
    private val dateFormat: DateFormat = SimpleDateFormat("HH:mm")
    private val daysSimpleName: Array<String?> = mFragment.resources.getStringArray(R.array.days_simple_name)
    private val themeManager: ThemeManager = ThemeManager.getInstance()

    @JvmField
    var isEditMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntriesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_entries_item, parent, false)
        return EntriesViewHolder(view, themeManager.getThemeDarkColor(mFragment.activity))
    }

    override fun getItemCount(): Int {
        return entriesList.size
    }

    override fun onBindViewHolder(holder: EntriesViewHolder, position: Int) {
        val calendar = Calendar.getInstance()
        calendar.setTime(entriesList[position]!!.createDate)

        if (showHeader(position)) {
            holder.header!!.visibility = View.VISIBLE
            holder.header.text = (calendar.get(Calendar.MONTH) + 1).toString()
        } else {
            holder.header!!.visibility = View.GONE
        }

        holder.tVDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        holder.tVDay.text = daysSimpleName[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        holder.tVTime.text = dateFormat.format(calendar.getTime())
        holder.tVTitle.text = entriesList[position]!!.title
        holder.tVSummary.text = entriesList[position]!!.summary

        holder.iVWeather.setImageResource(
            getWeatherResourceId(
                entriesList[position]!!.weatherId
            )
        )
        holder.iVMood.setImageResource(getMoodResourceId(entriesList[position]!!.moodId))

        if (entriesList[position]!!.hasAttachment()) {
            holder.iVAttachment.setVisibility(View.VISIBLE)
        } else {
            holder.iVAttachment.setVisibility(View.GONE)
        }
    }

    private fun showHeader(position: Int): Boolean {
        return if (position == 0) {
            true
        } else {
            val previousCalendar: Calendar = GregorianCalendar()
            previousCalendar.setTime(entriesList[position - 1]!!.createDate)
            val currentCalendar: Calendar = GregorianCalendar()
            currentCalendar.setTime(entriesList[position]!!.createDate)
            if (previousCalendar.get(Calendar.YEAR) != currentCalendar.get(Calendar.YEAR)) {
                true
            } else {
                previousCalendar.get(Calendar.MONTH) != currentCalendar.get(Calendar.MONTH)
            }
        }
    }

    open inner class EntriesViewHolder(rootView: View, @ColorInt color: Int) :
        RecyclerView.ViewHolder(rootView), View.OnClickListener, OnLongClickListener {
        val header: TextView? = rootView.findViewById<TextView?>(R.id.TV_entries_item_header)
        val tVDate: TextView = rootView.findViewById<TextView>(R.id.TV_entries_item_date)
        val tVDay: TextView = rootView.findViewById<TextView>(R.id.TV_entries_item_day)
        val tVTime: TextView = rootView.findViewById<TextView>(R.id.TV_entries_item_time)
        val tVTitle: TextView = rootView.findViewById<TextView>(R.id.TV_entries_item_title)
        val tVSummary: TextView = rootView.findViewById<TextView>(R.id.TV_entries_item_summary)

        val iVWeather: ImageView = rootView.findViewById<ImageView>(R.id.IV_entries_item_weather)
        val iVMood: ImageView = rootView.findViewById<ImageView>(R.id.IV_entries_item_mood)
        val iVBookmark: ImageView = rootView.findViewById<ImageView>(R.id.IV_entries_item_bookmark)
        val iVAttachment: ImageView = rootView.findViewById<ImageView>(R.id.IV_entries_item_attachment)

        val rLContent: RelativeLayout? = rootView.findViewById<RelativeLayout?>(R.id.RL_entries_item_content)

        init {
            this.itemView.setOnClickListener(this)
            this.itemView.setOnLongClickListener(this)

            initThemeColor(color)
        }

        private fun initThemeColor(@ColorInt color: Int) {
            this.tVDate.setTextColor(color)
            this.tVDay.setTextColor(color)
            this.tVTime.setTextColor(color)
            this.tVTitle.setTextColor(color)
            this.tVSummary.setTextColor(color)

            this.iVWeather.setColorFilter(color)
            this.iVMood.setColorFilter(color)
            this.iVBookmark.setColorFilter(color)
            this.iVAttachment.setColorFilter(color)
        }

        override fun onClick(v: View?) {
            //single click to open diary
            val diaryViewerDialog =
                newInstance(
                    entriesList[getAdapterPosition()]!!.id,
                    isEditMode
                )
            diaryViewerDialog.setTargetFragment(mFragment, 0)
            //Revert the icon
            if (isEditMode) {
                mFragment.setEditModeUI(isEditMode)
            }
            diaryViewerDialog.show(mFragment.requireFragmentManager(), "diaryViewerDialog")
        }

        override fun onLongClick(v: View?): Boolean {
            //Long click is always going to edit diary
            val diaryViewerDialog =
                newInstance(
                    entriesList[getAdapterPosition()]!!.id,
                    true
                )
            diaryViewerDialog.setTargetFragment(mFragment, 0)
            //Revert the icon
            if (isEditMode) {
                mFragment.setEditModeUI(isEditMode)
            }
            diaryViewerDialog.show(mFragment.requireFragmentManager(), "diaryViewerDialog")
            return true
        }
    }
}
