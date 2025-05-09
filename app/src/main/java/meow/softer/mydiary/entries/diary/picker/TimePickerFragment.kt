package meow.softer.mydiary.entries.diary.picker

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.shared.ThemeManager
import java.util.Calendar

class TimePickerFragment : DialogFragment() {
    private var savedTime: Long = 0
    private var onTimeSetListener: OnTimeSetListener? = null


    fun setOnTimeSetListener(onTimeSetListener: OnTimeSetListener?) {
        this.onTimeSetListener = onTimeSetListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedTime = requireArguments().getLong("savedTime", -1)
        val calendar = Calendar.getInstance()
        if (savedTime != -1L) {
            calendar.setTimeInMillis(savedTime)
        }
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        //Note:
        //Error/TimePickerDelegate: Unable to find keycodes for AM and PM.
        //The bug was triggered only on Chinese.
        return TimePickerDialog(
            activity, ThemeManager.getInstance().getPickerStyle(),
            onTimeSetListener, hour, minute, true
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(savedTime: Long): TimePickerFragment {
            val args = Bundle()
            val fragment = TimePickerFragment()
            args.putLong("savedTime", savedTime)
            fragment.setArguments(args)
            return fragment
        }
    }
}
