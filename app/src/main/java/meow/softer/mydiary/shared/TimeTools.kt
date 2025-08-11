package meow.softer.mydiary.shared

import android.content.Context
import meow.softer.mydiary.R

class TimeTools private constructor(context: Context) {
    val monthsFullName: Array<String?>? = context.resources.getStringArray(R.array.months_full_name)
    val daysFullName: Array<String?>?

    init {
        daysFullName = context.resources.getStringArray(R.array.days_full_name)
    }

    companion object {
        private var instance: TimeTools? = null

        fun getInstance(context: Context): TimeTools {
            if (instance == null) {
                instance = TimeTools(context)
            }
            return instance!!
        }
    }
}
