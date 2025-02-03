package meow.softer.mydiary.entries.diary.picker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import meow.softer.mydiary.shared.ThemeManager;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    private long savedTime;
    private DatePickerDialog.OnDateSetListener onDateSetListener;


    public static DatePickerFragment newInstance(long savedTime) {
        Bundle args = new Bundle();
        DatePickerFragment fragment = new DatePickerFragment();
        args.putLong("savedTime", savedTime);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar;
        savedTime = getArguments().getLong("savedTime", -1);
        calendar = Calendar.getInstance();
        if (savedTime != -1) {
            calendar.setTimeInMillis(savedTime);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), ThemeManager.getInstance().getPickerStyle(),
                onDateSetListener, year, month, day);
    }
}
