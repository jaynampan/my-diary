package meow.softer.mydiary.entries.diary.picker

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import meow.softer.mydiary.shared.ThemeManager
import java.util.Calendar

class DatePickerFragment : DialogFragment() {
    private var savedTime: Long = 0
    private var onDateSetListener: OnDateSetListener? = null


    fun setOnDateSetListener(onDateSetListener: OnDateSetListener?) {
        this.onDateSetListener = onDateSetListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        savedTime = requireArguments().getLong("savedTime", -1)
        val calendar = Calendar.getInstance()
        if (savedTime != -1L) {
            calendar.setTimeInMillis(savedTime)
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireActivity(), ThemeManager.getInstance().getPickerStyle(),
            onDateSetListener, year, month, day
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(savedTime: Long): DatePickerFragment {
            val args = Bundle()
            val fragment = DatePickerFragment()
            args.putLong("savedTime", savedTime)
            fragment.setArguments(args)
            return fragment
        }
    }
}
