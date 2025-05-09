package meow.softer.mydiary.entries.entry

import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar
import java.util.Date

class EntriesEntity(
    @JvmField val id: Long, val createDate: Date, val title: String?,
    val weatherId: Int, val moodId: Int, private val hasAttachment: Boolean
) : Comparable<CalendarDay> {
    @JvmField
    var summary: String? = null


    fun hasAttachment(): Boolean {
        return hasAttachment
    }

    override fun compareTo(calendarDay: CalendarDay): Int {
        val cal = Calendar.getInstance()
        cal.setTime(createDate)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return calendarDay.getCalendar().getTimeInMillis().compareTo(
            cal.getTimeInMillis()
        )
    }
}
