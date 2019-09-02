package com.jash.cybernetics;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Create a time picker
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private int hour = -1;
    private int minute = -1;
    private Context context;
    TimePickerFragment(Context context) {
        this.context = context;
        hour = -1;
        minute = -1;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minuteN) {
        Calendar c = Calendar.getInstance();
        if (hourOfDay < c.get(Calendar.HOUR_OF_DAY) || minuteN < c.get(Calendar.MINUTE)) {
            Toast alert = Toast.makeText(context, "Seleccione una hora igual o posterior a la actual", Toast.LENGTH_SHORT);
            alert.show();
            return;
        }
        minute = minuteN;
        hour = hourOfDay;
    }


    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }
}
