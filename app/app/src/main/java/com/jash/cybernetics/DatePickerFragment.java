package com.jash.cybernetics;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

// Create a date picker
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int year;
    private int month;
    private int day;
    private Context context;

    DatePickerFragment(Context context) {
        this.context = context;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        if (year < c.get(Calendar.YEAR) || month < Calendar.MONTH || day < Calendar.DAY_OF_MONTH)
        {
            Toast alert = Toast.makeText(context, "Seleccione una fecha igual o posterior a la actual", Toast.LENGTH_SHORT);
            alert.show();
            return;
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
