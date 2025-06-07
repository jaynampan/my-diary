package meow.softer.mydiary.entries.calendar

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.CalendarMode
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import meow.softer.mydiary.R
import meow.softer.mydiary.entries.BaseDiaryFragment
import meow.softer.mydiary.entries.DiaryActivity
import meow.softer.mydiary.shared.ThemeManager
import java.util.Calendar
import java.util.Collections
import java.util.Date

class CalendarFragment : BaseDiaryFragment(), View.OnClickListener, OnDateSelectedListener,
    DayViewDecorator {
    /**
     * Calendar
     */
    private var calendar: Calendar? = null
    private var currentDate: Date? = null

    private var themeManager: ThemeManager? = null

    /**
     * UI
     */
    private var RL_calendar_content: RelativeLayout? = null
    private var RL_calendar_edit_bar: RelativeLayout? = null
    private var FAB_calendar_change_mode: FloatingActionButton? = null

    /**
     * calendar Mode
     */
    private var pageEffectView: PageEffectView? = null
    private var materialCalendarView: MaterialCalendarView? = null

    private var currentMode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
        currentDate = Date()
        calendar!!.setTime(currentDate)
        themeManager = ThemeManager.instance!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_calendar, container, false)

        RL_calendar_edit_bar = rootView.findViewById<RelativeLayout>(R.id.RL_calendar_edit_bar)
        RL_calendar_edit_bar!!.setBackgroundColor(themeManager!!.getThemeMainColor(requireContext()))

        RL_calendar_content = rootView.findViewById<RelativeLayout>(R.id.RL_calendar_content)

        FAB_calendar_change_mode =
            rootView.findViewById<FloatingActionButton>(R.id.FAB_calendar_change_mode)
        //Set the color
        FAB_calendar_change_mode!!.getDrawable()
            .setColorFilter(
                themeManager!!.getThemeMainColor(requireContext()),
                PorterDuff.Mode.SRC_ATOP
            )
        FAB_calendar_change_mode!!.setOnClickListener(this)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //default mode
        currentMode = MODE_DAY
        initCalendarMode()
    }

    fun refreshCalendar() {
        when (currentMode) {
            MODE_DAY -> {}
            MODE_MONTH -> materialCalendarView!!.invalidateDecorators()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.FAB_calendar_change_mode -> {
                //togle the mode
                currentMode = if (currentMode == MODE_DAY) {
                    MODE_MONTH
                } else {
                    MODE_DAY
                }
                initCalendarMode()
            }
        }
    }

    private fun initCalendarMode() {
        RL_calendar_content!!.removeAllViews()
        when (currentMode) {
            MODE_DAY -> {
                materialCalendarView = null
                pageEffectView = PageEffectView(requireActivity(), calendar!!)
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                pageEffectView!!.setLayoutParams(params)
                RL_calendar_content!!.addView(pageEffectView)
            }

            MODE_MONTH -> {
                pageEffectView = null
                materialCalendarView = MaterialCalendarView(activity)
                val calendarViewParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
                )
                materialCalendarView!!.setLayoutParams(calendarViewParams)
                materialCalendarView!!.showOtherDates = MaterialCalendarView.SHOW_ALL
                materialCalendarView!!.setSelectionColor(
                    ThemeManager.instance!!.getThemeMainColor(requireContext())
                )
                materialCalendarView!!.state().edit()
                    .setFirstDayOfWeek(Calendar.MONDAY)
                    .setCalendarDisplayMode(CalendarMode.MONTHS)
                    .commit()
                materialCalendarView!!.setCurrentDate(calendar)
                materialCalendarView!!.setDateSelected(calendar, true)
                materialCalendarView!!.setOnDateChangedListener(this)
                RL_calendar_content!!.addView(materialCalendarView)
                //Add view first , then add Decorator
                materialCalendarView!!.addDecorator(this)
            }
        }
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return Collections.binarySearch<CalendarDay?>(entriesList, day) >= 0
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, ThemeManager.instance!!.getThemeDarkColor(requireContext())))
    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        //Make calendar sync the new date
        calendar!!.setTime(date.getDate())

        //Goto the diary position
        val diaryPosition = Collections.binarySearch<CalendarDay?>(entriesList, date)
        if (diaryPosition >= 0) {
            (activity as DiaryActivity).callEntriesGotoDiaryPosition(diaryPosition)
        }
    }

    companion object {
        private const val MODE_DAY = 1
        private const val MODE_MONTH = 2
    }
}
